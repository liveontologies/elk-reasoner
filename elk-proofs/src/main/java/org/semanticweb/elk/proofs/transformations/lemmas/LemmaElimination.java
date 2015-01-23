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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.derived.SubPropertyChainExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialChainAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialLemmaChainComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialComposition;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Transforms EL+ inferences containing lemma premises, that is, {@link ExistentialLemmaChainComposition} and {@link ExistentialChainAxiomComposition} into
 * {@link NaryExistentialComposition} inferences without lemma premises. 
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class LemmaElimination implements Operations.Transformation<Inference, Iterable<Inference>> {

	private final ElkObjectFactory elkFactory_;
	
	private ReasonerInferenceReader reader_;
	
	public LemmaElimination(ReasonerInferenceReader reader) {
		elkFactory_ = new ElkObjectFactoryImpl();
		reader_ = reader;
	}
	
	private DerivedExpressionFactory getExpressionFactory() {
		return reader_.getExpressionFactory();
	}
	
	@Override
	public Iterable<Inference> transform(final Inference inference) {
		return new Iterable<Inference>() {

			@Override
			public Iterator<Inference> iterator() {
				return lazyElimination(inference);
			}
			
		};
	}
	
	private boolean lemmasPresent(Inference inf) {
		for (DerivedExpression premise : inf.getPremises()) {
			if (premise instanceof LemmaExpression) {
				return true;
			}
		}
		
		return false;
	}
	
	Iterator<Inference> lazyElimination(final Inference inf) {
		if (!lemmasPresent(inf)) {
			// a shortcut to avoid creating a queue, etc.
			return Collections.singletonList(inf).iterator();
		}
		
		final Queue<Inference> toDo = new ArrayDeque<Inference>();
		
		toDo.add(inf);
		// returning the lazy iterator which will recursively replace inferences
		// with lemmas by inferences without a single lemma premise 
		return new Iterator<Inference>() {

			Inference next = null;
			
			Iterator<Inference> nextTransformed = null;
			
			@Override
			public boolean hasNext() {
				for (;;) {
					if (next != null) {
						return true;
					}
					
					if (nextTransformed != null) {
						while (nextTransformed.hasNext()) {
							Inference candidate = nextTransformed.next();
							
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
					//FIXME
					//System.err.println("Rewriting " + candidate);
					
					// rewriting happens here
					nextTransformed = candidate.accept(new ClassInferenceRewriter(), null).iterator();
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

	/**
	 * Visitor which rewrites inferences
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private class ClassInferenceRewriter extends AbstractInferenceVisitor<Void, Iterable<Inference>> {
		
		@Override
		protected Iterable<Inference> defaultVisit(Inference inf, Void input) {
			// by default output the same inference, rewrite just some specific owns which may have lemmas
			return Collections.singletonList(inf);
		}

		@Override
		public Iterable<Inference> visit(final ExistentialChainAxiomComposition inf, Void input) {
			// check if there are premises not representable in OWL and transform to the n-ary inference if that's the case
			if (inf.getSecondExistentialPremise() instanceof LemmaExpression) {
				Inference transformed = new NaryExistentialComposition(
											inf.getConclusion(), 
											Arrays.asList(inf.getFirstExistentialPremise(), inf.getSecondExistentialPremise()),
											// can have only chain axioms here. can't have transitivity, for example, that doesn't involve lemmas
											(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) inf.getChainPremise());
				
				return Collections.singletonList(transformed);
			}
			
			return super.visit(inf, input);
		}

		@Override
		public Iterable<Inference> visit(final NaryExistentialComposition inf, Void input) {
			final List<DerivedExpression> commonPremises = new ArrayList<DerivedExpression>();
			
			for (int i = 0; i < inf.getExistentialPremises().size(); i++) {
				final int premiseIndex = i;
				DerivedExpression premise = inf.getExistentialPremises().get(i);
				
				if (premise instanceof LemmaExpression) {
					// replacing the current inference by a collection of inferences, one for each inference which derives the lemma premise
					List<Inference> transformed = new LinkedList<Inference>();
					
					try {
						for (Inference lemmaInf : premise.getInferences()) {
							transformed.add(lemmaInf.accept(new AbstractInferenceVisitor<Void, Inference>() {

								@Override
								protected Inference defaultVisit(Inference inference, Void input) {
									// shouldn't get here, check?
									return null;
								}

								@Override
								public Inference visit(ExistentialLemmaChainComposition lemmaInf, Void input) {
									// only this inference can derive existential lemma premises
									List<DerivedExpression> premises = new ArrayList<DerivedExpression>(commonPremises);
									
									premises.add(lemmaInf.getFirstExistentialPremise());
									premises.add(lemmaInf.getSecondExistentialPremise());
									
									boolean moreLemmas = lemmaInf.getSecondExistentialPremise() instanceof LemmaExpression;
									// copying the remaining inferences
									for (int j = premiseIndex + 1; j < inf.getExistentialPremises().size(); j++) {
										DerivedExpression nextPremise = inf.getExistentialPremises().get(j);
										
										moreLemmas |= (nextPremise instanceof LemmaExpression);
										premises.add(nextPremise);
									}
									
									NaryExistentialComposition rewritten = null;
									
									if (!moreLemmas) {
										rewritten = recreateChainAxiom(
												inf.getConclusion(), 
												premises,
												inf.getChainPremise().getAxiom());
										//FIXME
										//System.err.println("Lemma-free n-ary inference: " + rewritten);
									}
									else {
										rewritten = new NaryExistentialComposition(inf.getConclusion(), premises, inf.getChainPremise());	
									}
									
									return rewritten;
								}
								
							}, null));
						}
					} catch (ElkException e) {
						// TODO log it
						
					}
					
					return transformed;
				}
				else {
					// copying axiom premises
					commonPremises.add(premise);
				}
			}
			// shouldn't get here
			return defaultVisit(inf, input);
		}

		private NaryExistentialComposition recreateChainAxiom(
				DerivedAxiomExpression<ElkSubClassOfAxiom> conclusion, 
				List<? extends DerivedExpression> existentialPremises,
				ElkSubObjectPropertyOfAxiom chainPremise) {
			List<ElkObjectPropertyExpression> chainList = new ArrayList<ElkObjectPropertyExpression>(existentialPremises.size());
			
			for (DerivedExpression premise : existentialPremises) {
				ElkSubClassOfAxiom exPremise = (ElkSubClassOfAxiom) ((DerivedAxiomExpression<?>) premise).getAxiom();
				ElkObjectSomeValuesFrom exSuper = (ElkObjectSomeValuesFrom) exPremise.getSuperClassExpression();
				
				chainList.add(exSuper.getProperty());
			}
			
			return new NaryExistentialComposition(
					conclusion,
					existentialPremises,
					
					new SubPropertyChainExpression(
							getExpressionFactory().create(elkFactory_.getSubObjectPropertyOfAxiom(elkFactory_.getObjectPropertyChain(chainList), (ElkObjectProperty) chainPremise.getSuperObjectPropertyExpression())),
							chainPremise,
							reader_));
		}
		
	}
}
