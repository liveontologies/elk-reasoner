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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.transformations.LemmaElimination;
import org.semanticweb.elk.proofs.transformations.TransformedAxiomExpression;
import org.semanticweb.elk.proofs.utils.RecursiveInferenceVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;

/**
 * The main entrance point for accessing proofs.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofReader {

	// TODO support more than one (i.e. nested) transformation, it's easy
	private Operations.Transformation<Inference, Iterable<Inference>> inferenceTransformation_;
	
	private final ReasonerInferenceReader reader_;
	
	public ProofReader(Reasoner r) {
		reader_ = new ReasonerInferenceReader(r);
	}
	
	public ProofReader eliminateLemmas() {
		inferenceTransformation_ = new LemmaElimination(reader_.getExpressionFactory(), new ElkObjectFactoryImpl());
		
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
		
		if (inferenceTransformation_ != null) {
			root = new TransformedAxiomExpression<Transformation<Inference, Iterable<Inference>>>(root, inferenceTransformation_);
		}
		
		return root;
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
	public static InferenceGraph readInferenceGraph(ProofReader reader, ElkClassExpression subsumee, ElkClassExpression subsumer) throws ElkException {
		final InferenceGraphImpl graph = new InferenceGraphImpl();
		
		RecursiveInferenceVisitor.visitInferences(reader, subsumee, subsumer, new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				graph.addInference(inference);
				return null;
			}
		});
		
		return graph;
	}
	
}
