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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
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

	/**
	 * Starts reading proofs by retrieving the {@link DerivedExpression} which
	 * corresponds to the subsumption entailment between the give classes. The
	 * inferences can now be explored by calling
	 * {@code DerivedExpression#getInferences()} for each expression used as a
	 * premise.
	 * 
	 * @param reasoner
	 * @param subsumee
	 * @param subsumer
	 * @return
	 * @throws ElkException
	 */
	public static DerivedExpression start(Reasoner reasoner, ElkClassExpression subsumee,
			ElkClassExpression subsumer) throws ElkException {
		ReasonerInferenceReader reader = new ReasonerInferenceReader(reasoner);

		return reader.initialize(subsumee, subsumer);
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
	public static InferenceGraph readInferenceGraph(Reasoner reasoner, ElkClassExpression subsumee,
			ElkClassExpression subsumer) throws ElkException {
		final InferenceGraphImpl graph = new InferenceGraphImpl();
		
		RecursiveInferenceVisitor.visitInferences(reasoner, subsumee, subsumer, new AbstractInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Inference inference, Void input) {
				graph.addInference(inference);
				return null;
			}
		});
		
		return graph;
	}
	
	/**
	 * TODO
	 * 
	 * @param reasoner
	 * @param subsumee
	 * @param subsumer
	 * @return
	 * @throws ElkException
	 */
	public static ProofDependencyGraph readDependencyGraph(Reasoner reasoner, ElkClassExpression subsumee, ElkClassExpression subsumer) throws ElkException {
		final ProofDependencyGraph depGraph = new ProofDependencyGraph();
		final InferenceGraph infGraph = readInferenceGraph(reasoner, subsumee, subsumer);
		Queue<DerivedExpression> toDo = new LinkedList<DerivedExpression>(infGraph.getRootExpressions());
		Set<DerivedExpression> proved = new HashSet<DerivedExpression>(infGraph.getExpressions().size());
		
		for (;;) {
			DerivedExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			if (proved.add(next)) {
				//FIXME
				//System.err.println("Proved: " + next);
				
				for (Inference inf : infGraph.getInferencesForPremise(next)) {
					DerivedExpression conclusion = inf.getConclusion();
					
					if (depGraph.getExpressions().containsAll(inf.getPremises())) {
						Iterable<DerivedExpression> dependencies = Operations.concat(
								Operations.map(
										inf.getPremises(), new Transformation<DerivedExpression, Iterable<DerivedExpression>>() {

							@Override
							public Iterable<DerivedExpression> transform(DerivedExpression thePremise) {
								return depGraph.getDependencies(thePremise);
							}
						}));
						
						
						toDo.add(conclusion);
						depGraph.updateDependencies(inf.getConclusion(), dependencies);
					}
				}
			}
		}
		
		return depGraph;
	}
}
