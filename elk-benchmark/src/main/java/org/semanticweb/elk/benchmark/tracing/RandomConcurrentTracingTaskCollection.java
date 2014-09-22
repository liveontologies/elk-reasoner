/*
 * #%L
 * ELK Bencharking Package
 * 
 * $Id$
 * $HeadURL$
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
/**
 * 
 */
package org.semanticweb.elk.benchmark.tracing;

import java.io.File;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
import org.semanticweb.elk.benchmark.VisitorTaskCollection;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestUtils;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Instantiates a random collection of tracing tasks which are likely to be
 * inter-dependent (require the same contexts to be traced) and submits them for
 * tracing concurrently.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomConcurrentTracingTaskCollection implements VisitorTaskCollection {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(AllSubsumptionTracingTaskCollection.class);
	
	private static final int BATCH_SIZE = 100;
	
	final String ontologyFile_;
	Reasoner reasoner_;
	final ReasonerConfiguration reasonerConfig_;
	private final Metrics metrics_ = new Metrics();
	
	public RandomConcurrentTracingTaskCollection(String... args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public void visitTasks(final TaskVisitor visitor) throws TaskException {
		// classify the ontology and instantiate tracing tasks
		final Taxonomy<ElkClass> taxonomy = loadAndClassify(ontologyFile_);
		final Multimap<ElkClassExpression, ElkClassExpression> tracingBatch = new HashListMultimap<ElkClassExpression, ElkClassExpression>();
		final MutableInteger batchSize = new MutableInteger(0);
		
		try {
			new ComprehensiveSubsumptionTracingTests(taxonomy).accept(new TracingTestVisitor() {
				
				@Override
				public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) throws Exception {
					
					tracingBatch.add(subsumee, subsumer);
					
					if (batchSize.increment() >= BATCH_SIZE) {
						visitor.visit(createBatchTask(tracingBatch));	
						tracingBatch.clear();
						batchSize.set(0);
					}
					
					return true;
				}
			});
			
		} 
		catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TaskException(e);
		}
	}
	
	BatchTracingTask createBatchTask(Multimap<ElkClassExpression, ElkClassExpression> batch) {
		Multimap<ElkClassExpression, ElkClassExpression> batchCopy = new HashListMultimap<ElkClassExpression, ElkClassExpression>();
		
		for (ElkClassExpression sub : batch.keySet()) {
			for (ElkClassExpression sup : batch.get(sub)) {
				batchCopy.add(sub, sup);
			}
		}
		
		return new BatchTracingTask(reasoner_, batchCopy);
	}

/*	TracingTask createSpecificTask(String sub, String sup) {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		return createSpecificTask(factory.getClass(new ElkFullIri(sub)), factory.getClass(new ElkFullIri(sup)));
	}*/

	Taxonomy<ElkClass> loadAndClassify(String ontologyFile) throws TaskException {
		try {
			File ontFile = BenchmarkUtils.getFile(ontologyFile);

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			
			reasoner_ = new ReasonerFactory().createReasoner(loader,
					new SimpleStageExecutor(),
					//new RuleAndConclusionCountMeasuringExecutor(new SimpleStageExecutor(), metrics_),
					reasonerConfig_);
			
			Taxonomy<ElkClass> taxonomy = reasoner_.getTaxonomy();
			
			//TaxonomyPrinter.dumpClassTaxomomyToFile(taxonomy, "/home/pavel/tmp/galen.taxonomy", false);
			
			return taxonomy;
			
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public Metrics getMetrics() {
		return metrics_;
	}
	
	@Override
	public void dispose() {
		try {
			reasoner_.shutdown();
		} catch (InterruptedException e) {
			// who cares..
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 **/
	static class BatchTracingTask implements Task {
		
		public static final String USED_INFERENCES_COUNT = "used inferences";
		public static final String SUBCLASSOF_AXIOM_COUNT = "Distinct SubClassOf axioms used";
		public static final String RULES_APPLIED = "Number of rules applied during tracing";
		public static final String CONTEXTS_TRACED = "Number of contexts traced";
		public static final int MIN_SUBCLASS_AXIOM_NO = 0;
		public static final int MAX_SUBCLASS_AXIOM_NO = Integer.MAX_VALUE;

		final Reasoner reasoner;
		final Multimap<ElkClassExpression, ElkClassExpression> subsumptions_;
		final Metrics metrics = new Metrics();
		
		BatchTracingTask(Reasoner r, Multimap<ElkClassExpression, ElkClassExpression> subsumptions) {
			reasoner = r;
			subsumptions_ = subsumptions;
		}
		
		@Override
		public String getName() {
			return "Batch tracing";
		}

		@Override
		public void prepare() throws TaskException {
			metrics.reset();
			reasoner.resetTraceState();
			
			for (ElkClassExpression sub : subsumptions_.keySet()) {
				for (ElkClassExpression sup : subsumptions_.get(sub)) {
					reasoner.submitForTracing(sub, sup);
				}
			}
		}

		@Override
		public void run() throws TaskException {
			try {
				reasoner.trace();
				
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}
		
		@Override
		public void postRun() throws TaskException {
			try {
				for (ElkClassExpression sub : subsumptions_.keySet()) {
					for (ElkClassExpression sup : subsumptions_.get(sub)) {
						postProcessSubsumption(sub, sup);
					}
				}
			} catch (Exception e) {
				throw new TaskException(e);
			}
		}
		
		private void postProcessSubsumption(ElkClassExpression subsumee, ElkClassExpression subsumer) throws Exception {
			TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
			IndexedClassExpression sub = ReasonerStateAccessor.transform(reasoner, subsumee);
			IndexedClassExpression sup = ReasonerStateAccessor.transform(reasoner, subsumer);
			TraceStore.Reader inferenceReader = traceState.getTraceStore().getReader();
			TraceUnwinder traceUnwinder = new RecursiveTraceUnwinder(inferenceReader);
			SideConditionCollector counter = new SideConditionCollector();
			
			traceUnwinder.accept(sub, new DecomposedSubsumerImpl<IndexedClassExpression>(sup), new DummyConclusionVisitor<IndexedClassExpression, Void>(), counter, ObjectPropertyConclusionVisitor.DUMMY, ObjectPropertyInferenceVisitor.DUMMY);
			
			int subClassAxiomNo = counter.getSubClassOfAxioms().size();
			
			if ((subClassAxiomNo >= MIN_SUBCLASS_AXIOM_NO) && (subClassAxiomNo <= MAX_SUBCLASS_AXIOM_NO)) {
				metrics.incrementRunCount();
				metrics.updateLongMetric(SUBCLASSOF_AXIOM_COUNT, subClassAxiomNo);
				metrics.updateLongMetric(USED_INFERENCES_COUNT, counter.getInferenceCount());
			}
			else {
				//ignoring the results
			}
			
			if (TracingTestUtils.checkTracingCompleteness(subsumee, subsumer, reasoner) <= 0) {
				throw new TaskException("The subsumption " + subsumee + " => " + subsumer + " wasn't properly traced");
			}
			
			//TracingTestUtils.checkInferenceAcyclicity(reasoner);
		}

		@Override
		public void dispose() {
		}

		@Override
		public Metrics getMetrics() {
			return metrics;
		}
	}

}
