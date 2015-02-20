/**
 * 
 */
package org.semanticweb.elk.proofs;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.transformations.InferenceTransformation;
import org.semanticweb.elk.proofs.transformations.TransformedAxiomExpression;
import org.semanticweb.elk.proofs.transformations.lemmas.LemmaElimination;
import org.semanticweb.elk.proofs.utils.RecursiveInferenceVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 * The main entrance point for accessing proofs.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofReader {

	private LemmaElimination lemmaElimination_;
	// mandatory transformations which should be applied after lemma elimination
	// (after because lemma elimination can introduce new expressions whose
	// inferences must then be transformed)
	private final List<InferenceTransformation> transformations_;
	
	private final ReasonerInferenceReader reader_;
	
	
	public ProofReader(Reasoner r) {
		reader_ = new ReasonerInferenceReader(r);
		transformations_ = new LinkedList<InferenceTransformation>();
		//transformations_.add(new OneStepCyclicInferenceFilter());
	}
	
	public ProofReader eliminateLemmas() {
		lemmaElimination_ = new LemmaElimination(reader_);
		
		return this;
	}
	
	/**
	 * Starts reading proofs by retrieving the {@link DerivedExpression} which
	 * corresponds to the subsumption entailment between the give classes. The
	 * inferences can now be explored by calling
	 * {@code DerivedExpression#getInferences()} for each expression used as a
	 * premise.
	 * 
	 * @param subsumee
	 * @param subsumer
	 * @return
	 * @throws ElkException
	 */
	public DerivedAxiomExpression<?> getProofRoot(ElkClassExpression subsumee, ElkClassExpression subsumer) throws ElkException {
		DerivedAxiomExpression<?> root = reader_.initialize(subsumee, subsumer);
		
		return applyTransformation(root);
	}
	
	public DerivedAxiomExpression<?> getProofRootForInconsistency() throws ElkException {
		DerivedAxiomExpression<?> root = reader_.initializeForInconsistency();
		
		return applyTransformation(root);
	}
	
	private <E extends ElkAxiom> DerivedAxiomExpression<E> applyTransformation(DerivedAxiomExpression<E> root) {
		DerivedAxiomExpression<E> top = root;
		
		if (lemmaElimination_ != null) {
			top = new TransformedAxiomExpression<InferenceTransformation, E>(top, lemmaElimination_);
		}
		
		for (InferenceTransformation t : transformations_) {
			top = new TransformedAxiomExpression<InferenceTransformation, E>(top, t);
		}
		
		return top;
	}
	
	/**
	 * Retrieves the full inference graph in which each expression is mapped to inferences using it as a premise. 
	 * 
	 * @param reasoner
	 * @param subsumee
	 * @param subsumer
	 * @return
	 * @throws ElkException
	 */
	public static InferenceGraph readInferenceGraph(DerivedExpression root) throws ElkException {
		final InferenceGraphImpl graph = new InferenceGraphImpl();
		
		RecursiveInferenceVisitor.visitInferences(root, new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				graph.addInference(inference);
				return null;
			}
		});
		
		return graph;
	}
	
}
