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
package org.semanticweb.elk.benchmark.reasoning.tracing;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskCollection;
import org.semanticweb.elk.benchmark.TaskCollection2;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.BaseTracedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.FirstNInferencesReader;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.TRACE_MODE;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestUtils;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task to trace all atomic subsumptions
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AllSubsumptionTracingTaskCollection implements TaskCollection, TaskCollection2 {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(AllSubsumptionTracingTaskCollection.class);
	
	private final String ontologyFile_;
	private Reasoner reasoner_;
	private final ReasonerConfiguration reasonerConfig_;
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
				public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) throws Exception {
					
					visitor.visit(new TracingTask(reasoner_, subsumee, subsumer));
					
					return true;
				}
			});
			
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0039693", "http://purl.obolibrary.org/obo/GO_0044034"));
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0006176", "http://purl.obolibrary.org/obo/GO_0046031"));
		} 
		catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TaskException(e);
		}
	}
	
	@Override
	public Collection<Task> getTasks() throws TaskException {
		// classify the ontology and instantiate tracing tasks
		Taxonomy<ElkClass> taxonomy = loadAndClassify(ontologyFile_);
		
		LOGGER_.info("Ontology classified, creating tracing tasks..");
		
		// TODO lazy task collection would be better for performance, fix TaskCollection interface
		final List<Task> tasks = new LinkedList<Task>();
		
		try {
			new ComprehensiveSubsumptionTracingTests(taxonomy).accept(new TracingTestVisitor() {
				
				@Override
				public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) {
					
					tasks.add(new TracingTask(reasoner_, subsumee, subsumer));
					
					return true;
				}
			});
		} catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TaskException(e);
		}
		
		LOGGER_.info("Tasks created, re-shuffling..");
		
		Collections.shuffle(tasks);
		
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#HortonArteritis", "http://www.co-ode.org/ontologies/galen#PeripheralArterialDisease"));
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#ProstatismSymptom", "http://www.co-ode.org/ontologies/galen#UrinarySymptom"));
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#LeftAnteriorEthmoidSinus", "http://www.co-ode.org/ontologies/galen#LeftEthmoidSinus"));
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#SquamoColumnarJunctionOfCervix", "http://www.co-ode.org/ontologies/galen#ComponentOfUterineCervix"));
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#SkinOfBack", "http://www.co-ode.org/ontologies/galen#SurfaceStructureOfBack"));
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#BedOfIndexFingerNail", "http://www.co-ode.org/ontologies/galen#Anonymous-716"));
		//tasks.add(createSpecificTask("http://www.co-ode.org/ontologies/galen#T6Dermatome", "http://www.co-ode.org/ontologies/galen#Anonymous-716"));
		
		/*GO-EXT r7991*/
		//tasks.add(createSpecificTask("http://purl.obolibrary.org/obo/GO_0001801", "http://purl.obolibrary.org/obo/GO_0002894"));
		
		return tasks;
	}
	
	TracingTask createSpecificTask(String sub, String sup) {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		return new TracingTask(reasoner_, factory.getClass(new ElkFullIri(sub)), factory.getClass(new ElkFullIri(sup)));
	}

	private Taxonomy<ElkClass> loadAndClassify(String ontologyFile) throws TaskException {
		try {
			File ontFile = BenchmarkUtils.getFile(ontologyFile);

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			
			reasoner_ = new ReasonerFactory().createReasoner(loader,
					new SimpleStageExecutor(), reasonerConfig_);
			
			return reasoner_.getTaxonomy();
			
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
	 */
	private static class TracingTask implements Task {
		
		public static final String SUBCLASSOF_AXIOM_COUNT = "Distinct SubClassOf axioms used";
		public static final String RULES_APPLIED = "Number of rules applied during tracing";
		public static final String CONTEXTS_TRACED = "Number of contexts traced";
		public static final int MIN_SUBCLASS_AXIOM_NO = 0;
		public static final int MAX_SUBCLASS_AXIOM_NO = Integer.MAX_VALUE;

		final Reasoner reasoner;
		final ElkClassExpression subsumee;
		final ElkClassExpression subsumer;
		final Metrics metrics = new Metrics();
		
		TracingTask(Reasoner r, ElkClassExpression sub, ElkClassExpression sup) {
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
				reasoner.explainSubsumption(subsumee, subsumer, TRACE_MODE.RECURSIVE);
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
				Conclusion subsumerConclusion = TracingUtils.getSubsumerWrapper(sup);
				//TraceStore.Reader inferenceReader = traceState.getTraceStore().getReader();
				TraceStore.Reader inferenceReader = new FirstNInferencesReader(traceState.getTraceStore().getReader(), 1);
				RecursiveTraceUnwinder traceUnwinder = new RecursiveTraceUnwinder(inferenceReader);
				SideConditionCollector collector = new SideConditionCollector();
				SaturationStatistics stats = traceState.getContextTracingFactory().getStatistics();
				
				traceUnwinder.accept(sub.getContext(), subsumerConclusion, new BaseConclusionVisitor<Boolean, Context>(), collector);
				
				int subClassAxiomNo = collector.getSubClassOfAxioms().size();
				
				/*for (Pair<IndexedClassExpression, IndexedClassExpression> ax : collector.getSubClassOfAxioms()) {
					System.err.println("SubClassOf( " + ax.getFirst() +" " + ax.getSecond() + " )");
				}*/
				
				//if (!collector.getSubClassOfAxioms().contains(new Pair<IndexedClassExpression, IndexedClassExpression>(sub, sup))) {
				if (subClassAxiomNo >= MIN_SUBCLASS_AXIOM_NO && subClassAxiomNo <= MAX_SUBCLASS_AXIOM_NO) {
					metrics.incrementRunCount();
					metrics.updateLongMetric(SUBCLASSOF_AXIOM_COUNT, collector.getSubClassOfAxioms().size());
					metrics.updateLongMetric(RULES_APPLIED, stats.getConclusionStatistics().getProducedConclusionCounts().getTotalCount());
					metrics.updateLongMetric(CONTEXTS_TRACED, stats.getContextStatistics().countModifiedContexts);	
				}
				else {
					//ignoring the results
				}
				
				if (TracingTestUtils.checkTracingCompleteness(subsumee, subsumer, reasoner) <= 0) {
					throw new TaskException("The subsumption " + subsumee + " => " + subsumer + " wasn't properly traced");
				}
				
				TracingTestUtils.checkTracingMinimality(subsumee, subsumer, reasoner);
				
				collector.resetSubClassOfAxioms();
				
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
	
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class SideConditionCollector extends BaseTracedConclusionVisitor<Void, Context> {

		private Set<Pair<IndexedClassExpression, IndexedClassExpression>> subclassAxioms_ = new HashSet<Pair<IndexedClassExpression, IndexedClassExpression>>();
		
		//private final Set<IndexedClassExpression> fillers_ = new HashSet<IndexedClassExpression>();
		
		//private final Set<TracedConclusion> inferences = new HashSet<TracedConclusion>();
		
		@Override
		public Void visit(SubClassOfSubsumer conclusion, Context cxt) {
			subclassAxioms_.add(new Pair<IndexedClassExpression, IndexedClassExpression>(((Subsumer)conclusion.getPremise()).getExpression(), conclusion.getExpression()));
			
			//checkIfExistential(conclusion.getPremise());
			//checkIfExistential(conclusion);
			//inferences.add(conclusion);
			
			return super.visit(conclusion, cxt);
		}
		
		//collecting all fillers used in some existential restrictions
		private void checkIfExistential(Conclusion c) {
			if (c instanceof Subsumer) {
				IndexedClassExpression ice = ((Subsumer)c).getExpression();
				
				ice.accept(new IndexedClassExpressionVisitor<Void>() {

					@Override
					public Void visit(IndexedClass element) {
						return null;
					}

					@Override
					public Void visit(IndexedIndividual element) {
						return null;
					}

					@Override
					public Void visit(IndexedObjectComplementOf element) {
						return element.getNegated().accept(this);
					}

					@Override
					public Void visit(IndexedObjectIntersectionOf element) {
						element.getFirstConjunct().accept(this);
						element.getSecondConjunct().accept(this);
						return null;
					}

					@Override
					public Void visit(IndexedObjectSomeValuesFrom element) {
						//fillers_.add(element.getFiller());
						element.getFiller().accept(this);
						return null;
					}

					@Override
					public Void visit(IndexedObjectUnionOf element) {
						for (IndexedClassExpression disjunct : element.getDisjuncts()) {
							disjunct.accept(this);
						}
						return null;
					}

					@Override
					public Void visit(IndexedDataHasValue element) {
						return null;
					}
					
				});
			}
		}

		public Set<Pair<IndexedClassExpression, IndexedClassExpression>> getSubClassOfAxioms() {
			//System.err.println(subclassAxioms_.size());
			
			return subclassAxioms_;
		}
		
		public void resetSubClassOfAxioms() {
			subclassAxioms_ = null;
		}
		
		public Set<IndexedClassExpression> getFillers() {
			return null;//fillers_;
		}
	}

}
