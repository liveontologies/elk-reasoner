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
package org.semanticweb.elk.benchmark.reasoning.owlapi;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskCollection2;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
import org.semanticweb.elk.benchmark.reasoning.tracing.SideConditionCollector;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.modularity.OntologySegmenter;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.ExplanationProgressMonitor;

/**
 * A task to trace all atomic subsumptions
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class FindJustificationsForAllSubsumptions implements TaskCollection2 {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(FindJustificationsForAllSubsumptions.class);
	
	private static final int JUSTIFICATION_NO_THRESHOLD = 1;
	
	final String ontologyFile_;
	private Reasoner reasoner_;
	private final ReasonerConfiguration reasonerConfig_;
	private final Metrics aggregateMetrics_ = new Metrics();
	final OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
	final OWLOntologyManager manager;
	final OWLDataFactory factory;
	OWLOntology ontology;
	OntologySegmenter extractor;
	Taxonomy<ElkClass> taxonomy;
	
	public FindJustificationsForAllSubsumptions(String... args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
		manager = OWLManager.createOWLOntologyManager();
		factory = OWLManager.getOWLDataFactory();
	}
	
	void initOWLOntology() throws Exception {
		if (ontology == null) {
			ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile_));
			extractor = new SyntacticLocalityModuleExtractor(manager, ontology, ModuleType.STAR);
		}
	}
	
	@Override
	public void visitTasks(final TaskVisitor visitor) throws TaskException {
		// classify the ontology and instantiate tracing tasks
		if (taxonomy == null) {
			taxonomy = loadAndClassify(ontologyFile_);
		}
		
		try {
			initOWLOntology();
			
			new ComprehensiveSubsumptionTracingTests(taxonomy).accept(new TracingTestVisitor() {
				
				@Override
				public boolean visit(ElkClassExpression subsumee, 	ElkClassExpression subsumer) throws Exception {
					
					visitor.visit(createSpecificTask(subsumee, subsumer));
					
					return true;
				}
			});
			 
			//visitor.visit(createSpecificTask("http://www.co-ode.org/ontologies/galen#CerebellarSyndrome", "http://www.co-ode.org/ontologies/galen#Anonymous-757"));
			//visitor.visit(createSpecificTask("http://www.co-ode.org/ontologies/galen#SignOfAscites", "http://www.co-ode.org/ontologies/galen#Sign"));
				
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0039693", "http://purl.obolibrary.org/obo/GO_0044034")); //55 axioms, now 15
			//visitor.visit(createSpecificTask("http://purl.obolibrary.org/obo/GO_0006176", "http://purl.obolibrary.org/obo/GO_0046031")); //197 axioms, now 19
		} 
		catch (TaskException e) {
			throw e;
		}
		catch (Exception e) {
			throw new TaskException(e);
		}
	}
	
	GenerateJustifications createSpecificTask(ElkClassExpression sub, ElkClassExpression sup) {
		return new GenerateJustifications(sub, sup, JUSTIFICATION_NO_THRESHOLD);
	}
	
	GenerateJustifications createSpecificTask(String sub, String sup) {
		ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();
		return new GenerateJustifications(elkFactory.getClass(new ElkFullIri(sub)), elkFactory.getClass(new ElkFullIri(sup)), JUSTIFICATION_NO_THRESHOLD);
	}

	Taxonomy<ElkClass> loadAndClassify(String ontologyFile) throws TaskException {
		try {
			File ontFile = BenchmarkUtils.getFile(ontologyFile);

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			
			reasoner_ = new ReasonerFactory().createReasoner(loader,
					new SimpleStageExecutor(), reasonerConfig_);
			
			Taxonomy<ElkClass> taxonomy = reasoner_.getTaxonomy();
			
			return taxonomy;
			
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public Metrics getMetrics() {
		return aggregateMetrics_;
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
	class GenerateJustifications implements Task {
		
		public static final String SUBCLASSOF_AXIOM_COUNT = "distinct subsumptions used in traces";
		public static final String JUSTIFICATION_COUNT = "justifications found";
		public static final String AVG_JUSTIFICATION_SIZE = "average size of justifications for a subsumption";
		public static final String TOTAL_JUSTIFICATION_SIZE = "total size of justifications for a subsumption";
		public static final String MODULE_SIZE = "number of subsumptions in the module";
		//the max number of justifications to generate
		final int justificationsNo;
		final OWLSubClassOfAxiom axiomToExplain;
		final Metrics metrics = new Metrics();
		private final ElkClassExpression subsumee_;
		private final ElkClassExpression subsumer_;
		
		OWLOntology module = null;
		
		GenerateJustifications(ElkClassExpression sub, ElkClassExpression sup, int threshold) {
			IRI subIri = IRI.create(((ElkClass)sub).getIri().getFullIriAsString());
			IRI supIri = IRI.create(((ElkClass)sup).getIri().getFullIriAsString());
			
			justificationsNo = threshold;
			axiomToExplain = factory.getOWLSubClassOfAxiom(factory.getOWLClass(subIri), factory.getOWLClass(supIri));
			subsumee_ = sub;
			subsumer_ = sup;
		}
		
		@Override
		public String getName() {
			return "Generating justifications ";// + axiomToExplain;
		}

		private void doTracing() throws ElkException {
			reasoner_.resetTraceState();
			reasoner_.explainSubsumption(subsumee_, subsumer_);
			//collect all useful metrics
			TraceState traceState = ReasonerStateAccessor.getTraceState(reasoner_);
			IndexedClassExpression sub = ReasonerStateAccessor.transform(reasoner_, subsumee_);
			IndexedClassExpression sup = ReasonerStateAccessor.transform(reasoner_, subsumer_);
			Conclusion subsumerConclusion = TracingUtils.getSubsumerWrapper(sup);
			TraceStore.Reader inferenceReader = traceState.getTraceStore().getReader();
			RecursiveTraceUnwinder traceUnwinder = new RecursiveTraceUnwinder(inferenceReader);
			SideConditionCollector collector = new SideConditionCollector();
			//SaturationStatistics stats = traceState.getContextTracingFactory().getStatistics();
			
			traceUnwinder.accept(sub.getContext(), subsumerConclusion, new BaseConclusionVisitor<Boolean, Context>(), collector);
			
			int subClassAxiomNo = collector.getSubClassOfAxioms().size();
			
			metrics.updateLongMetric(SUBCLASSOF_AXIOM_COUNT, subClassAxiomNo);
		}
		
		/**
		 * 
		 */
		void extractModule() throws Exception {
			String iri = "http://test.org/" + System.currentTimeMillis();
			Set<OWLAxiom> moduleAxioms = extractor.extract(axiomToExplain.getSignature());
			int subsumptionCounter = 0;
			//counting only subsumption axioms (equivalences should have been rewritten)
			for (OWLAxiom ax : moduleAxioms) {
				if (ax.isOfType(AxiomType.SUBCLASS_OF, AxiomType.OBJECT_PROPERTY_DOMAIN)) {
					subsumptionCounter++;
				}
			}
			
			metrics.updateLongMetric(MODULE_SIZE, subsumptionCounter);
			
			module = OWLManager.createOWLOntologyManager().createOntology(moduleAxioms, IRI.create(iri));
		}
		
		/**
		 * 
		 */
		void computeJustifications() throws Exception {
			final Set<OWLAxiom> subclassAxioms = new HashSet<OWLAxiom>();
			final MutableInteger counter = new MutableInteger(0);
			
			DefaultExplanationGenerator multExplGen = new DefaultExplanationGenerator(module.getOWLOntologyManager(), reasonerFactory, module, new ExplanationProgressMonitor() {
				
				@Override
				public boolean isCancelled() {
					return counter.get() >= justificationsNo;
				}
				
				@Override
				public void foundExplanation(Set<OWLAxiom> arg0) {
					counter.increment();
					
					for (OWLAxiom ax : arg0) {
						if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
							subclassAxioms.add((OWLSubClassOfAxiom)ax);
						}

					}
				}
				
				@Override
				public void foundAllExplanations() {
					// TODO 
				}
			});
			
			multExplGen.getExplanations(axiomToExplain, justificationsNo);
			
			metrics.updateLongMetric(TOTAL_JUSTIFICATION_SIZE, subclassAxioms.size());
			metrics.updateDoubleMetric(AVG_JUSTIFICATION_SIZE, subclassAxioms.size() * 1.0d / counter.get());
			metrics.updateLongMetric(JUSTIFICATION_COUNT, counter.get());
		}
		
		@Override
		public void prepare() throws TaskException {
			metrics.reset();
			
			try {
				metrics.incrementRunCount();
				
				//doTracing();
				//extractModule();
				//System.err.println("module extracted, " + module.getAxiomCount() + " axioms");
				
			} catch (Throwable e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void run() throws TaskException {
			try {
				extractModule();
				//computeJustifications();
				
			} catch (Throwable e) {
				throw new TaskException(e);
			}
			finally {
				module = null;
			}
		}
		
		@Override
		public void postRun() throws TaskException {
			
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
