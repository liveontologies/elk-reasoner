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
package org.semanticweb.elk.benchmark.proofs;

import java.io.File;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
import org.semanticweb.elk.benchmark.VisitorTaskCollection;
import org.semanticweb.elk.benchmark.tracing.AllSubsumptionTracingTaskCollection;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.proofs.ProofReader;
import org.semanticweb.elk.proofs.utils.TestUtils;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestVisitor;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * A task collection to reconstruct proofs for all atomic subsumptions in the classified ontology.
 * 
 * Used only for correctness at this point, no timing just yet.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofsForAllSubsumptionTaskCollection implements VisitorTaskCollection {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(ProofsForAllSubsumptionTaskCollection.class);
	
	private final String ontologyFile_;
	
	private Reasoner reasoner_;
	
	private final ReasonerConfiguration reasonerConfig_;
	
	private final Metrics metrics_ = new Metrics();
	
	public ProofsForAllSubsumptionTaskCollection(String... args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public void visitTasks(final TaskVisitor visitor) throws TaskException {
		// classify the ontology and instantiate tracing tasks
		Taxonomy<ElkClass> taxonomy = loadAndClassify(ontologyFile_);
		
		// classify the ontology and instantiate proof reconstruction tasks
		//final OWLDataFactory factory = OWLManager.getOWLDataFactory();
		// loading and classifying via the OWL API
		//final OWLOntology ontology = loadOntology();
		
		//reasoner_ = OWLAPITestUtils.createReasoner(ontology, reasonerConfig_);
		
		try {
			// this visitor checks binding of premises to axioms in the source ontology
	        /*final OWLInferenceVisitor bindingChecker = ProofTestUtils.getAxiomBindingChecker(ontology);
	        
	        ProofTestUtils.visitAllSubsumptionsForProofTests(reasoner_, new ProofTestVisitor<TaskException>() {
				
				@Override
				public void visit(OWLClassExpression subsumee, OWLClassExpression subsumer) throws TaskException {
					visitor.visit(new ProofTask(factory.getOWLSubClassOfAxiom(subsumee, subsumer), bindingChecker));
				}
			});*/
			new ComprehensiveSubsumptionTracingTests(taxonomy).accept(new TracingTestVisitor() {
				
				@Override
				public boolean visit(ElkClass subsumee, ElkClass subsumer) throws Exception {
					
					visitor.visit(new ProofTask(reasoner_, subsumee, subsumer));
					
					return true;
				}
			});
			
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}
	
	/*private OWLOntology loadOntology() throws TaskException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;

		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile_));
		} catch (Exception e) {
			throw new TaskException(e);
		}
		
		return ontology;
	}*/

	@Override
	public Metrics getMetrics() {
		return metrics_;
	}

	@Override
	public void dispose() {
		if (reasoner_ != null) {
			try {
				reasoner_.shutdown();
			} catch (InterruptedException e) {
				// oh well
			}
		}
	}
	
	Taxonomy<ElkClass> loadAndClassify(String ontologyFile) throws TaskException {
		try {
			File ontFile = BenchmarkUtils.getFile(ontologyFile);

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			
			reasoner_ = new ReasonerFactory().createReasoner(loader,
					new SimpleStageExecutor(),
					reasonerConfig_);
			
			Taxonomy<ElkClass> taxonomy = reasoner_.getTaxonomy();
			
			return taxonomy;
			
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}	
	
	/**
	 * 
	 */
	private static class ProofTask extends AllSubsumptionTracingTaskCollection.TracingTask {

		ProofTask(Reasoner r, ElkClassExpression sub, ElkClassExpression sup) {
			super(r, sub, sup);
		}

		@Override
		public String getName() {
			return String.format("Proof tracing"/*"Proof tracing %s <= %s"*/, subsumee, subsumer);
		}

		@Override
		public void run() throws TaskException {
			try {
				ReasonerStateAccessor.cleanClassTraces(reasoner);
				
				TestUtils.provabilityTest(new ProofReader(reasoner).eliminateLemmas(), subsumee, subsumer);
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void postRun() throws TaskException {
			// no-op
		}
		
		
	}
}
