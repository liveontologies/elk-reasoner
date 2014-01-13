package org.semanticweb.elk.benchmark.reasoning;

/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.benchmark.AllFilesTaskCollection;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.stages.AbstractStageExecutor;
import org.semanticweb.elk.reasoner.stages.IncrementalClassTaxonomyComputationStage;
import org.semanticweb.elk.reasoner.stages.ReasonerStage;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.stages.RuleAndConclusionCountMeasuringExecutor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.stages.TimingStageExecutor;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * Incrementally classifies an ontology wrt multiple deltas. Expects a folder
 * with a single file (the initial version of the ontology) and multiple folders
 * with additions and deletions (with suffixes ADDITION_SUFFIX and
 * DELETION_SUFFIX, resp.)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationMultiDeltas extends
		AllFilesTaskCollection {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory.getLogger(IncrementalClassificationMultiDeltas.class);
	
	private static final String ADDITION_SUFFIX = "delta-plus";
	private static final String DELETION_SUFFIX = "delta-minus";
	public static final String DELETED_AXIOM_COUNT = "deleted-axioms.count";
	public static final String ADDED_AXIOM_COUNT = "added-axioms.count";

	protected Reasoner reasoner;
	protected AdditionDeletionListener stageExecutor;
	protected final ReasonerConfiguration config;
	protected final Metrics metrics = new Metrics();

	public IncrementalClassificationMultiDeltas(String[] args) {
		super(args);
		config = getConfig(args);
	}

	private ReasonerConfiguration getConfig(String[] args) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		if (args.length > 1) {
			config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
					args[1]);
		}

		return config;
	}

	@Override
	public Task instantiateSubTask(String[] args) throws TaskException {
		File source = new File(args[0]);

		if (!source.exists()) {
			throw new TaskException("Wrong source file/dir " + args[0]);
		}

		if (source.isFile()) {
			if (reasoner != null) {
				dispose();
			}
			// initial classification, argument is the first ontology
			return getFirstTimeClassificationTask(source);
		} else {
			// incremental classification, argument is a folder with the
			// positive and the negative delta
			return getIncrementalClassificationTask(source);
		}
	}

	protected Task getFirstTimeClassificationTask(File source) {
		return new ClassifyFirstTime(source);
	}

	protected Task getIncrementalClassificationTask(File source) {
		return new ClassifyIncrementally(source);
	}

	@Override
	protected File[] sortFiles(File[] files) {
		// There should be one file and multiple dirs.
		// the file should go first, the rest should be sorted by name
		File file = null;
		File[] result = new File[files.length];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				result[i] = files[i];
			} else {
				file = files[i];
			}
		}

		Arrays.sort(result, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if (o1 == null) {
					return -1;
				} else if (o2 == null) {
					return 1;
				} else {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});

		result[0] = file;

		return result;
	}

	@Override
	public Metrics getMetrics() {
		return metrics;
	}

	@Override
	public void dispose() {

		try {
			if (reasoner != null) {
				reasoner.shutdown();
				reasoner = null;
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Classifies the initial version of the ontology
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class ClassifyFirstTime implements Task {

		private final File ontologyFile_;

		ClassifyFirstTime(File file) {
			ontologyFile_ = file;
		}

		@Override
		public String getName() {
			return "Classify first ontology: " + ontologyFile_.getName();
		}

		@Override
		public void prepare() throws TaskException {
			stageExecutor =
			
			//new StatsExecutor(new SimpleStageExecutor(), metrics);
			//new StatsExecutor(new PostProcessingStageExecutor(), metrics);
			new TimingExecutor(new SimpleStageExecutor(), metrics);
			
			// always start with a new reasoner
			createReasoner();
		}

		@Override
		public void run() throws TaskException {
			try {
				reasoner.getTaxonomyQuietly();
			} catch (ElkException e) {
				throw new TaskException(e);
			}
			finally {
				stageExecutor.reset();
			}
		}

		protected void createReasoner() throws TaskException {
			InputStream stream;
			try {
				stream = new FileInputStream(ontologyFile_);
			} catch (FileNotFoundException e) {
				throw new TaskException(e);
			}
			try {
				AxiomLoader loader = new Owl2StreamLoader(
						new Owl2FunctionalStyleParserFactory(), stream);
				reasoner = new ReasonerFactory().createReasoner(loader,
						stageExecutor, config);
				reasoner.setAllowIncrementalMode(false);
			} catch (Exception e) {
				throw new TaskException(e);
			} finally {
				IOUtils.closeQuietly(stream);
			}
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return metrics;
		}
		
		@Override
		public void postRun() throws TaskException {}
	}

	/**
	 * Applies the deltas for the next version
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	protected class ClassifyIncrementally implements Task {

		private final File deltaDir_;

		ClassifyIncrementally(File dir) {
			deltaDir_ = dir;
		}

		@Override
		public String getName() {
			return "Classify incrementally";
		}

		@Override
		public void prepare() throws TaskException {
			// load positive and negative deltas
			reasoner.setAllowIncrementalMode(true);

			loadChanges(reasoner);
		}

		protected void loadChanges(Reasoner reasoner) throws TaskException {
			final TestChangesLoader loader = new TestChangesLoader();
			final AxiomCountingProcessor addProcessor = new AxiomCountingProcessor(loader, true);
			final AxiomCountingProcessor removeProcessor = new AxiomCountingProcessor(loader, false);

			reasoner.registerAxiomLoader(loader);

			load(ADDITION_SUFFIX, addProcessor);
			
			/*if (addProcessor.getAxiomCounter() > 0) {
				metrics.updateLongMetric(ADDED_AXIOM_COUNT, addProcessor.getAxiomCounter());
			}*/
			
			stageExecutor.notifyAdditionCount((int)addProcessor.getAxiomCounter());
			load(DELETION_SUFFIX, removeProcessor);
			
			/*if (removeProcessor.getAxiomCounter() > 0) {
				metrics.updateLongMetric(DELETED_AXIOM_COUNT, removeProcessor.getAxiomCounter());
			}*/
			
			stageExecutor.notifyDeletionCount((int)removeProcessor.getAxiomCounter());
			// measure only for revisions with both additions and deletions
			if (addProcessor.getAxiomCounter() > 0 && removeProcessor.getAxiomCounter() > 0) {
				metrics.updateLongMetric(ADDED_AXIOM_COUNT, addProcessor.getAxiomCounter());
				metrics.updateLongMetric(DELETED_AXIOM_COUNT, removeProcessor.getAxiomCounter());
			}
		}

		private void load(final String suffix,
			final ElkAxiomProcessor elkAxiomProcessor) throws TaskException {
			File[] diffs = deltaDir_.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(suffix);
				}
			});

			if (diffs.length != 1) {
				throw new TaskException("Cannot find deltas");
			}

			InputStream stream = null;

			try {
				stream = new FileInputStream(diffs[0]);

				new Owl2FunctionalStyleParserFactory().getParser(stream)
						.accept(new Owl2ParserAxiomProcessor() {

							@Override
							public void visit(ElkPrefix elkPrefix)
									throws Owl2ParseException {
							}

							@Override
							public void visit(ElkAxiom elkAxiom)
									throws Owl2ParseException {
								elkAxiomProcessor.visit(elkAxiom);
							}

							@Override
							public void finish() throws Owl2ParseException {
							}
						});

			} catch (Exception e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void run() throws TaskException {
			try {
				reasoner.getTaxonomyQuietly();
				metrics.incrementRunCount();
			} catch (ElkException e) {
				throw new TaskException(e);
			}
			finally {
				//Statistics.logMemoryUsage(LOGGER_);
				stageExecutor.reset();
			}
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return metrics;
		}
		
		@Override
		public void postRun() throws TaskException {}
		
		/*
		 * 
		 */
		private class AxiomCountingProcessor implements ElkAxiomProcessor {
			
			private final TestChangesLoader loader_;
			private long counter_ = 0;
			private final boolean add_;
			
			AxiomCountingProcessor(TestChangesLoader l, boolean add) {
				loader_ = l;
				add_ = add;
			}
			
			@Override
			public void visit(ElkAxiom elkAxiom) {
				if (add_) {
					loader_.add(elkAxiom);
				}
				else {
					loader_.remove(elkAxiom);
				}
				
				counter_++;
			}
			
			long getAxiomCounter() {
				return counter_;
			}
		}
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	interface AdditionDeletionListener extends ReasonerStageExecutor {
		
		void notifyAdditionCount(int addCount);
		
		void notifyDeletionCount(int delCount);
		
		void reset();
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	static class StatsExecutor extends RuleAndConclusionCountMeasuringExecutor implements AdditionDeletionListener {

		protected int lastAddCount = 0;
		
		protected int lastDelCount = 0;
		
		private SaturationStatistics totalStats_ = new SaturationStatistics();
		
		public StatsExecutor(AbstractStageExecutor e, Metrics m) {
			super(e, m);
		}

		@Override
		public void notifyAdditionCount(int addCount) {
			//System.err.println(addCount + " additions");
			lastAddCount = addCount;
		}

		@Override
		public void notifyDeletionCount(int delCount) {
			//System.err.println(delCount + " deletions");
			lastDelCount = delCount;
		}
		
		@Override
		protected void doMeasure(ReasonerStage stage, SaturationStatistics stats) {
			recordMetrics(stage.getName(), stats);
			
			totalStats_.add(stats);
			stats.reset();
			
			if (stage.getClass().equals(IncrementalClassTaxonomyComputationStage.class)) {
				recordMetrics("Total", totalStats_);
			}
		}
		
		

		@Override
		protected void executeStage(ReasonerStage stage,
				SaturationStatistics stats) throws ElkException {
			
			long ts = CachedTimeThread.getCurrentTimeMillis();

			super.executeStage(stage, stats);
			
			ts = CachedTimeThread.getCurrentTimeMillis() - ts;
			
			if (measure(stage)) {				
				metrics.updateLongMetric(stage.getName() + ".wall-time", ts);
				
				/*if (stage.getClass().equals(IncrementalDeletionInitializationStage.class)) {
					System.err.println("Del init time: " + ts);
				}*/
			}
		}

		@Override
		protected boolean measure(ReasonerStage stage) {
			return lastAddCount > 0 && lastDelCount > 0;
		}

		@Override
		public void reset() {
			totalStats_ = new SaturationStatistics();
		}
		
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	static class TimingExecutor extends TimingStageExecutor implements AdditionDeletionListener {

		public TimingExecutor(AbstractStageExecutor executor, Metrics m) {
			super(executor, m);
		}

		protected int lastAddCount = 0;
		
		protected int lastDelCount = 0;
		
		private long total_ = 0;
		
		@Override
		public void notifyAdditionCount(int addCount) {
			lastAddCount = addCount;
		}

		@Override
		public void notifyDeletionCount(int delCount) {
			lastDelCount = delCount;
		}
		
		@Override
		public void reset() {
			total_ = 0;
		}

		@Override
		public void execute(ReasonerStage stage) throws ElkException {
			long ts = System.currentTimeMillis();

			executeStage(stage);
			ts = System.currentTimeMillis() - ts;

			if (lastAddCount > 0 && lastDelCount > 0) {
				total_ += ts;

				metrics.updateLongMetric(stage.getName() + WALL_TIME, ts);

				if (stage.getClass().equals(
						IncrementalClassTaxonomyComputationStage.class)) {
					metrics.updateLongMetric("total" + WALL_TIME, total_);
				}
			}
		}
		
	}	
}
