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
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.semanticweb.elk.benchmark.AllFilesTaskCollection;
import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

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
public class IncrementalClassificationMultiDeltasTask extends AllFilesTaskCollection {

	private static String ADDITION_SUFFIX = "delta-plus";
	private static String DELETION_SUFFIX = "delta-minus";	
	
	private Reasoner reasoner_;
	private Reasoner standardReasoner_;
	private final ReasonerConfiguration config_;
	
	static final boolean CHECK_CORRECTNESS = false;
	static final boolean INCREMENTAL_RUNS = true;
	
	public IncrementalClassificationMultiDeltasTask(String[] args) {
		super(args);
		config_ = getConfig(args);		
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
			if (reasoner_ != null || standardReasoner_ != null) {
				dispose();
			}
			// initial classification, argument is the first ontology
			return new ClassifyFirstTime(args[0]);
		}
		else {
			//incremental classification, argument is a folder with the positive and the negative delta
			return INCREMENTAL_RUNS ? new ClassifyIncrementally(args[0]) : new ClassifyNonIncrementally(args[0]);
		}
	}
	
	
	@Override
	protected File[] sortFiles(File[] files) {
		//There should be one file and multiple dirs.
		//the file should go first, the rest should be sorted by name
		File file = null;
		File[] result = new File[files.length];
		
		for (int i = 0; i <  files.length; i++) {
			if (files[i].isDirectory()) {
				result[i] = files[i];
			}
			else {
				file = files[i];
			}
		}
		
		Arrays.sort(result, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if (o1 == null) {
					return -1;
				}
				else if (o2 == null) {
					return 1;
				}
				else {
					return o1.getName().compareTo(o2.getName());
				}
			}});

		result[0] = file;
		
		return result;
	}

	@Override
	public void dispose() {
		try {
			if (reasoner_ != null) {
				reasoner_.shutdown();
				reasoner_ = null;
			}
			
			if (standardReasoner_ != null) {
				standardReasoner_.shutdown();
				standardReasoner_ = null;
			}
		} catch (InterruptedException e) {
		}
	}

	
	/**
	 * Classifies the initial version of the ontology
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ClassifyFirstTime implements Task {

		private final File ontologyFile_;
		
		ClassifyFirstTime(String file) {
			ontologyFile_ = new File(file);
			
			if (CHECK_CORRECTNESS) {
				standardReasoner_ = new ReasonerFactory().createReasoner(new SimpleStageExecutor(), config_);
			}
		}
		
		@Override
		public String getName() {
			return "Classify first ontology: " + ontologyFile_.getName();
		}

		@Override
		public void prepare() throws TaskException {
			//always start with a new reasoner
			reasoner_ = new ReasonerFactory().createReasoner(new TimingStageExecutor(new SimpleStageExecutor()), config_);			
			load(reasoner_);
			
			if (CHECK_CORRECTNESS) {
				load(standardReasoner_);
			}
		}

		@Override
		public Result run() throws TaskException {
			Taxonomy<ElkClass> incrementalTaxonomy = reasoner_.getTaxonomyQuietly();
			
			if (CHECK_CORRECTNESS) {
				Taxonomy<ElkClass> standardTaxonomy = standardReasoner_
						.getTaxonomyQuietly();

				Assert.assertEquals(TaxonomyHasher.hash(incrementalTaxonomy),
						TaxonomyHasher.hash(standardTaxonomy));
			}
			
			return null;
		}
		
		private void load(Reasoner reasoner) throws TaskException {
			InputStream stream = null;
			
			try {
				stream = new FileInputStream(ontologyFile_);
				reasoner.setIncrementalMode(false);
				reasoner.registerOntologyLoader(new Owl2StreamLoader(
						new Owl2FunctionalStyleParserFactory(), stream));
				reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());
				reasoner.loadOntology();
			} catch (Exception e) {
				throw new TaskException(e);
			}
			finally {
				IOUtils.closeQuietly(stream);
			}
		}

		@Override
		public void dispose() {
		}
	}
	
	/**
	 * Applies the deltas for the next version
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ClassifyIncrementally implements Task {

		private final File deltaDir_;
		
		ClassifyIncrementally(String dir) {
			deltaDir_ = new File(dir);
		}
		
		@Override
		public String getName() {
			return "Classify incrementally";
		}

		@Override
		public void prepare() throws TaskException {
			
			// load positive and negative deltas
			reasoner_.setIncrementalMode(true);
			
			loadChanges(reasoner_);
			
			if (CHECK_CORRECTNESS) {
				standardReasoner_.setIncrementalMode(false);
				loadChanges(standardReasoner_);
			}
		}
		
		protected void loadChanges(Reasoner reasoner) throws TaskException {
			final TestChangesLoader loader = new TestChangesLoader();
			
			load(ADDITION_SUFFIX, new ElkAxiomProcessor() {
				
				@Override
				public void visit(ElkAxiom elkAxiom) {
					loader.add(elkAxiom);
				}
			});
			
			load(DELETION_SUFFIX, new ElkAxiomProcessor() {
				
				@Override
				public void visit(ElkAxiom elkAxiom) {
					loader.remove(elkAxiom);
				}
			});
			
			try {
				
				reasoner.registerOntologyChangesLoader(loader);
				reasoner.loadChanges();			
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}

		private void load(final String suffix, final ElkAxiomProcessor elkAxiomProcessor) throws TaskException {
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
				
				new Owl2FunctionalStyleParserFactory().getParser(stream).accept(new Owl2ParserAxiomProcessor() {
					
					@Override
					public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
					}
					
					@Override
					public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
						elkAxiomProcessor.visit(elkAxiom);					
					}
				});
				
			} catch (Exception e) {
				throw new TaskException(e);
			}
		}

		@Override
		public Result run() throws TaskException {
			Taxonomy<ElkClass> incrementalTaxonomy = reasoner_.getTaxonomyQuietly();
			
			if (CHECK_CORRECTNESS) {
				Taxonomy<ElkClass> standardTaxonomy = standardReasoner_
						.getTaxonomyQuietly();

				Assert.assertEquals(TaxonomyHasher.hash(incrementalTaxonomy),
						TaxonomyHasher.hash(standardTaxonomy));
			}
			
			return null;
		}

		@Override
		public void dispose() {
		}	
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ClassifyNonIncrementally extends ClassifyIncrementally {

		ClassifyNonIncrementally(String dir) {
			super(dir);
		}

		@Override
		public String getName() {
			return "Classify from scratch";
		}

		@Override
		public void prepare() throws TaskException {
			reasoner_.setIncrementalMode(false);
			loadChanges(reasoner_);
			reasoner_.setIncrementalMode(false);
		}
		
	}
}
