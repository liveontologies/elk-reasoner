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

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
import org.semanticweb.elk.benchmark.VisitorTaskCollection;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestUtils;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.reasoner.stages.RuleAndConclusionCountMeasuringExecutor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * A task to trace all atomic subsumptions
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AllSubsumptionTracingTaskCollection implements VisitorTaskCollection {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(AllSubsumptionTracingTaskCollection.class);
	
	final String ontologyFile_;
	Reasoner reasoner_;
	final ReasonerConfiguration reasonerConfig_;
	private final Metrics metrics_ = new Metrics();
	
	public AllSubsumptionTracingTaskCollection(String... args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public void visitTasks(final TaskVisitor visitor) throws TaskException {
		// classify the ontology and instantiate tracing tasks
		Taxonomy<ElkClass> taxonomy = loadAndClassify(ontologyFile_);
		
		try {
			new ComprehensiveSubsumptionTracingTests(taxonomy).accept(new TracingTestVisitor() {
				
				@Override
				public boolean visit(ElkClass subsumee, ElkClass subsumer) throws Exception {
					
					visitor.visit(createSpecificTask(subsumee, subsumer));
					
					return true;
				}
			});
			
			//visitor.visit(createSpecificTask("http://www.co-ode.org/ontologies/galen#BNFChapter5_5Section", "http://www.co-ode.org/ontologies/galen#BNFChapter5Section"));
			//visitor.visit(createSpecificTask("http://www.co-ode.org/ontologies/galen#CerebellarSyndrome", "http://www.co-ode.org/ontologies/galen#Anonymous-757"));
			//visitor.visit(createSpecificTask("http://www.co-ode.org/ontologies/galen#BigeminalPulseRhythm", "http://www.co-ode.org/ontologies/galen#CardiacDysrhythmia"));
			
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0039693", "http://purl.obolibrary.org/obo/GO_0044034")); //55 axioms, now 15
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0006176", "http://purl.obolibrary.org/obo/GO_0046031")); //197 axioms, now 19
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0034223", "http://purl.obolibrary.org/obo/GO_0034307"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_1901548", "http://purl.obolibrary.org/obo/GO_0032849"));
			
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0048684", "http://purl.obolibrary.org/obo/GO_0048672"));
			
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0043404", "http://purl.obolibrary.org/obo/GO_0001653"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0001653", "http://purl.obolibrary.org/obo/BFO_0000003"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0033232", "http://purl.obolibrary.org/obo/GO_1901243"));
			
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0048685", "http://purl.obolibrary.org/obo/GO_0048671"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0048685", "http://purl.obolibrary.org/obo/GO_0048683"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0048684", "http://purl.obolibrary.org/obo/GO_0048683"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0048685", "http://purl.obolibrary.org/obo/GO_0048681"));
			
			
		} 
		catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TaskException(e);
		}
	}
	
	TracingTask createSpecificTask(ElkClassExpression sub, ElkClassExpression sup) {
		return new TracingTask(reasoner_, sub, sup);
	}

	TracingTask createSpecificTask(String sub, String sup) {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		return createSpecificTask(factory.getClass(new ElkFullIri(sub)), factory.getClass(new ElkFullIri(sup)));
	}

	Taxonomy<ElkClass> loadAndClassify(String ontologyFile) throws TaskException {
		try {
			File ontFile = BenchmarkUtils.getFile(ontologyFile);

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			
			reasoner_ = new ReasonerFactory().createReasoner(loader,
					/*new SimpleStageExecutor(),*/
					new RuleAndConclusionCountMeasuringExecutor(new SimpleStageExecutor(), metrics_),
					reasonerConfig_);
			
			Taxonomy<ElkClass> taxonomy = reasoner_.getTaxonomy();
			
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
	public static class TracingTask implements Task {
		
		public static final String USED_INFERENCES_COUNT = "used inferences";
		public static final String SUBCLASSOF_AXIOM_COUNT = "Distinct SubClassOf axioms used";
		public static final String RULES_APPLIED = "Number of rules applied during tracing";
		public static final String CONTEXTS_TRACED = "Number of contexts traced";
		public static final int MIN_SUBCLASS_AXIOM_NO = 0;
		public static final int MAX_SUBCLASS_AXIOM_NO = Integer.MAX_VALUE;

		protected final Reasoner reasoner;
		protected final ElkClassExpression subsumee;
		protected final ElkClassExpression subsumer;
		protected final Metrics metrics = new Metrics();
		
		protected TracingTask(Reasoner r, ElkClassExpression sub, ElkClassExpression sup) {
			reasoner = r;
			subsumee = sub;
			subsumer = sup;
		}
		
		@Override
		public String getName() {
			return "Subsumption tracing ";// + subsumee + " => " + subsumer;
		}

		@Override
		public void prepare() throws TaskException {
			metrics.reset();
		}

		@Override
		public void run() throws TaskException {
			try {
				reasoner.resetTraceState();
				reasoner.explainSubsumption(subsumee, subsumer);
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}
		
		@Override
		public void postRun() throws TaskException {
			try {
				TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner);
				IndexedClassExpression sub = ReasonerStateAccessor.transform(reasoner, subsumee);
				IndexedClassExpression sup = ReasonerStateAccessor.transform(reasoner, subsumer);
				TraceStore.Reader inferenceReader = traceState.getTraceStore().getReader();
				//TraceStore.Reader inferenceReader = new FirstNInferencesReader(traceState.getTraceStore().getReader(), 1);
				RecursiveTraceUnwinder traceUnwinder = new RecursiveTraceUnwinder(inferenceReader);
				SideConditionCollector counter = new SideConditionCollector();
				//SaturationStatistics stats = traceState.getContextTracingFactory().getStatistics();
				
				traceUnwinder.accept(sub, new DecomposedSubsumerImpl<IndexedClassExpression>(sup), counter);
				
				int subClassAxiomNo = counter.getSubClassOfAxioms().size();
				
				if ((subClassAxiomNo >= MIN_SUBCLASS_AXIOM_NO) && (subClassAxiomNo <= MAX_SUBCLASS_AXIOM_NO)) {
					metrics.incrementRunCount();
					metrics.updateLongMetric(SUBCLASSOF_AXIOM_COUNT, subClassAxiomNo);
					//metrics.updateLongMetric(RULES_APPLIED, stats.getConclusionStatistics().getProducedConclusionCounts().getTotalCount());
					//metrics.updateLongMetric("inserted conclusions", stats.getConclusionStatistics().getUsedConclusionCounts().getTotalCount());
					//metrics.updateLongMetric(CONTEXTS_TRACED, stats.getContextStatistics().countModifiedContexts);
					metrics.updateLongMetric(USED_INFERENCES_COUNT, counter.getInferenceCount());
				}
				else {
					//ignoring the results
				}
				
				if (TracingTestUtils.checkTracingCompleteness(subsumee, subsumer, reasoner) <= 0) {
					throw new TaskException("The subsumption " + subsumee + " => " + subsumer + " wasn't properly traced");
				}
				
				TracingTestUtils.checkTracingMinimality(subsumee, subsumer, reasoner);
				TracingTestUtils.checkInferenceAcyclicity(reasoner);
				
			} catch (Exception e) {
				throw new TaskException(e);
			}
			
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
