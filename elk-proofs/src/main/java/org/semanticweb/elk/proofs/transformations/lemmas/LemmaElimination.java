/**
 * 
 */
package org.semanticweb.elk.proofs.transformations.lemmas;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialChainAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialLemmaChainComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialLemmaComposition;
import org.semanticweb.elk.proofs.transformations.InferenceTransformation;
import org.semanticweb.elk.proofs.utils.ProofUtils;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 * This transformation re-writes existential composition inferences into those which do not use derived sub-property chain axioms.
 * It is based on {@link DerivedChainSubsumptionElimination} which does all the heavy lifting and
 * returns a lazy iterator over the sequence of transformed lemma-free inferences.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class LemmaElimination implements InferenceTransformation {
	/**
	 * The main transformation applied during the elimination.
	 */
	private final DerivedChainSubsumptionElimination rewritingUnderChainHierarchy_;
	
	public LemmaElimination(ReasonerInferenceReader reader) {
		rewritingUnderChainHierarchy_ = new DerivedChainSubsumptionElimination(reader);
	}
	
	@Override
	public Iterable<? extends Inference> transform(final Inference inference) {
		return inference.accept(new InferenceRewriter(), null);
	}

	private Iterable<Inference> lazyLemmaElimination(final Iterable<? extends Inference> inferences) {
		return new Iterable<Inference>() {

			@Override
			public Iterator<Inference> iterator() {
				return lazyEliminationIterator(inferences);
			}
			
		};
	}
	
	Iterator<Inference> lazyEliminationIterator(final Iterable<? extends Inference> inferences) {
		final Queue<Inference> toDo = new ArrayDeque<Inference>();
		// returning the lazy iterator which will recursively replace inferences
		// with lemmas by inferences without a single lemma premise 
		return new Iterator<Inference>() {

			Inference next = null;
			
			Iterator<? extends Inference> candidates = inferences.iterator();
			
			@Override
			public boolean hasNext() {
				for (;;) {
					if (next != null) {
						return true;
					}
					
					if (candidates != null) {
						while (candidates.hasNext()) {
							Inference candidate = candidates.next();
							
							if (lemmasPresent(candidate)) {
								toDo.add(candidate);
							}
							else {
								// found the next lemma-free inference
								next = candidate;
								break;
							}
						}
						
						if (next != null) {
							return true;
						}
					}
					
					Inference candidate = toDo.poll();
					
					if (candidate == null) {
						break;
					}
					
					NaryExistentialAxiomComposition transformed = candidate.accept(new TransformToNaryCompositions(), null);
					// rewriting happens here
					candidates = rewrite(transformed).iterator();
				}
				
				return false;
			}

			@Override
			public Inference next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				
				Inference result = next;

				next = null;
				
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	private boolean lemmasPresent(Inference inf) {
		for (Expression premise : inf.getPremises()) {
			if (premise instanceof LemmaExpression) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * The central rewriting method.
	 * 
	 * @param inf Inference for which some premises are lemmas.  
	 * @return A sequence of lemma-free inferences.
	 */
	public Iterable<NaryExistentialAxiomComposition> rewrite(final NaryExistentialAxiomComposition inf) {
		final List<Expression> commonPremises = new ArrayList<Expression>();
		
		for (int i = 0; i < inf.getExistentialPremises().size(); i++) {
			final int premiseIndex = i;
			Expression premise = inf.getExistentialPremises().get(i);
			
			if (premise instanceof LemmaExpression) {
				List<NaryExistentialAxiomComposition> transformed = new LinkedList<NaryExistentialAxiomComposition>();
				
				for (Inference lemmaInf : ProofUtils.getInferences(premise)) {
					transformed.add(lemmaInf.accept(new AbstractInferenceVisitor<Void, NaryExistentialAxiomComposition>() {

						@Override
						public NaryExistentialAxiomComposition visit(ExistentialLemmaChainComposition lemmaInf, Void input) {
							List<Expression> premises = new ArrayList<Expression>(commonPremises);
							ExistentialLemmaChainComposition expandedUnderHierarchy = rewritingUnderChainHierarchy_.transform(lemmaInf);

							premises.add(expandedUnderHierarchy.getFirstExistentialPremise());
							premises.add(expandedUnderHierarchy.getSecondExistentialPremise());

							// copying the remaining inferences
							for (int j = premiseIndex + 1; j < inf.getExistentialPremises().size(); j++) {
								Expression nextPremise = inf.getExistentialPremises().get(j);

								premises.add(nextPremise);
							}

							return new NaryExistentialAxiomComposition(inf.getConclusion(), premises, inf.getChainPremise());
						}

						@Override
						public NaryExistentialAxiomComposition visit(NaryExistentialLemmaComposition lemmaInf, Void input) {
							List<Expression> premises = new ArrayList<Expression>(commonPremises);

							premises.addAll(lemmaInf.getExistentialPremises());

							// copying the remaining inferences
							for (int j = premiseIndex + 1; j < inf.getExistentialPremises().size(); j++) {
								Expression nextPremise = inf.getExistentialPremises().get(j);

								premises.add(nextPremise);
							}

							return new NaryExistentialAxiomComposition(inf.getConclusion(), premises, inf.getChainPremise());
						}

					}, null));
				}
				
				return transformed;
			}
			else {
				// copying axiom premises
				commonPremises.add(premise);
			}
		}
		
		return Collections.singletonList(inf);
	}	

	/**
	 * 
	 * An auxiliary visitor which transforms inferences with lemmas to
	 * {@link NaryExistentialAxiomComposition}s which are then rewritten to
	 * eliminate lemmas.
	 * 
	 * @author Pavel Klinov pavel.klinov@uni-ulm.de
	 *
	 */
	private class TransformToNaryCompositions extends AbstractInferenceVisitor<Void, NaryExistentialAxiomComposition> {

		@Override
		public NaryExistentialAxiomComposition visit(ExistentialComposition inf, Void input) {
			NaryExistentialAxiomComposition transformed = new NaryExistentialAxiomComposition(
					inf.getConclusion(), 
					Collections.singletonList(inf.getExistentialPremise()), 
					inf.getSubPropertyPremise());

			return transformed;
		}

		@Override
		public NaryExistentialAxiomComposition visit(ExistentialChainAxiomComposition inf, Void input) {
			NaryExistentialAxiomComposition transformed = new NaryExistentialAxiomComposition(
					inf.getConclusion(), 
					Arrays.asList(inf.getFirstExistentialPremise(), inf.getSecondExistentialPremise()),
					// can have only chain axioms here. can't have transitivity, for example, that doesn't involve lemmas
					(AxiomExpression<ElkSubObjectPropertyOfAxiom>) inf.getChainPremise());

			return transformed;
		}

		@Override
		public NaryExistentialAxiomComposition visit(NaryExistentialAxiomComposition inf, Void input) {
			return inf;
		}
		
	}
	
	/**
	 * Visitor which begins the lazy lemma elimination process for existential and composition inferences.
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private class InferenceRewriter extends AbstractInferenceVisitor<Void, Iterable<? extends Inference>> {
		
		@Override
		protected Iterable<Inference> defaultVisit(Inference inf, Void input) {
			// by default output the same inference, rewrite just some specific ones which may have lemmas
			return Collections.singletonList(inf);
		}
		
		@Override
		public Iterable<? extends Inference> visit(final ExistentialComposition inf, Void input) {
			return lazyLemmaElimination(rewritingUnderChainHierarchy_.transform(inf)); 
		}

		@Override
		public Iterable<? extends Inference> visit(ExistentialChainAxiomComposition inf, Void input) {
			return lazyLemmaElimination(Collections.singleton(rewritingUnderChainHierarchy_.transform(inf)));
		}

	}

	@Override
	public boolean mayIntroduceDuplicates() {
		// TODO figure out exactly why and whether it can be fixed
		return true;
	}
}
