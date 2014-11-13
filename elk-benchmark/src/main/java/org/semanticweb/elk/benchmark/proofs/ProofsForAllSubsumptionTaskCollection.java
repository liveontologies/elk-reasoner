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

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.TaskVisitor;
import org.semanticweb.elk.benchmark.VisitorTaskCollection;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.owlapi.proofs.OWLInferenceVisitor;
import org.semanticweb.elk.owlapi.proofs.ProofTestUtils;
import org.semanticweb.elk.owlapi.proofs.ProofTestVisitor;
import org.semanticweb.elk.owlapi.proofs.RecursiveInferenceVisitor;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;

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
	
	private ExplainingOWLReasoner reasoner_;
	
	private final ReasonerConfiguration reasonerConfig_;
	
	private final Metrics metrics_ = new Metrics();
	
	public ProofsForAllSubsumptionTaskCollection(String... args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public void visitTasks(final TaskVisitor visitor) throws TaskException {
		// classify the ontology and instantiate proof reconstruction tasks
		final OWLDataFactory factory = OWLManager.getOWLDataFactory();
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology();
		
		reasoner_ = OWLAPITestUtils.createReasoner(ontology, reasonerConfig_);
		
		try {
			// this visitor checks binding of premises to axioms in the source ontology
	        final OWLInferenceVisitor bindingChecker = ProofTestUtils.getAxiomBindingChecker(ontology);
	        
	        ProofTestUtils.visitAllSubsumptionsForProofTests(reasoner_, new ProofTestVisitor<TaskException>() {
				
				@Override
				public void visit(OWLClassExpression subsumee, OWLClassExpression subsumer) throws TaskException {
					visitor.visit(new ProofTask(factory.getOWLSubClassOfAxiom(subsumee, subsumer), bindingChecker));
				}
			});
			
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}
	
	private OWLOntology loadOntology() throws TaskException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;

		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile_));
		} catch (Exception e) {
			throw new TaskException(e);
		}
		
		return ontology;
	}

	@Override
	public Metrics getMetrics() {
		return metrics_;
	}

	@Override
	public void dispose() {
		if (reasoner_ != null) {
			reasoner_.dispose();
		}
	}
	
	/**
	 * 
	 */
	private class ProofTask implements Task {

		private final Metrics taskMetrics_ = new Metrics();
		
		private final OWLSubClassOfAxiom entailment_;
		
		private final OWLInferenceVisitor checker_;
		
		ProofTask(OWLSubClassOfAxiom ax, OWLInferenceVisitor checker) {
			entailment_ = ax;
			checker_ = checker;
		}
		
		@Override
		public String getName() {
			//return "Get proofs for " + entailment_;
			return "Get proofs";
		}

		@Override
		public void prepare() throws TaskException {
			//no-op
		}

		@Override
		public void run() throws TaskException {
			try {
				// check that proofs can be reconstructed
				ProofTestUtils.provabilityTest(reasoner_, entailment_);
				// also count inferences
				final MutableInteger counter = new MutableInteger(0);
				
				RecursiveInferenceVisitor.visitInferences(reasoner_, entailment_, new OWLInferenceVisitor() {
					
					@Override
					public void visit(OWLInference inference) {
						checker_.visit(inference);
						counter.increment();
					}
				}, true);
				
				taskMetrics_.incrementRunCount();
				taskMetrics_.updateLongMetric("inferences.count", counter.get());
				
			} catch (ProofGenerationException e) {
				throw new TaskException(e);
			}
		}

		@Override
		public void dispose() {
			//no-op
		}

		@Override
		public Metrics getMetrics() {
			return taskMetrics_;
		}

		@Override
		public void postRun() throws TaskException {
		}
		
	}
}
