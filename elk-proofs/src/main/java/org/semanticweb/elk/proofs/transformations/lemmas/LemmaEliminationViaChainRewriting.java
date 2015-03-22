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
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionFactory;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialChainAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialLemmaChainComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialAxiomComposition;
import org.semanticweb.elk.proofs.transformations.InferenceTransformation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 *
 * TODO
 * 
 * This transformation produces derived sub-property chain axioms which are then explained.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class LemmaEliminationViaChainRewriting implements InferenceTransformation {

	private final ElkObjectFactory elkFactory_;
	
	private ReasonerInferenceReader reader_;
	
	public LemmaEliminationViaChainRewriting(ReasonerInferenceReader reader) {
		elkFactory_ = new ElkObjectFactoryImpl();
		reader_ = reader;
	}
	
	private ExpressionFactory getExpressionFactory() {
		return reader_.getExpressionFactory();
	}
	
	@Override
	public Iterable<Inference> transform(final Inference inference) {
		return inference.accept(new InferenceRewriter(), null);
	}
	

	/**
	 * Visitor which rewrites inferences
	 * 
	 * @author	Pavel Klinov
	 * 			pavel.klinov@uni-ulm.de
	 *
	 */
	private class InferenceRewriter extends AbstractInferenceVisitor<Void, Iterable<Inference>> {
		
		private boolean lemmasPresent(Inference inf) {
			for (Expression premise : inf.getPremises()) {
				if (premise instanceof LemmaExpression) {
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		protected Iterable<Inference> defaultVisit(Inference inf, Void input) {
			// by default output the same inference, rewrite just some specific owns which may have lemmas
			return Collections.singletonList(inf);
		}
		
		Iterator<Inference> lazyEliminationIterator(final NaryExistentialAxiomComposition inf) {
			final Queue<NaryExistentialAxiomComposition> toDo = new ArrayDeque<NaryExistentialAxiomComposition>();
			
			toDo.add(inf);
			// returning the lazy iterator which will recursively replace inferences
			// with lemmas by inferences without a single lemma premise 
			return new Iterator<Inference>() {

				Inference next = null;
				
				Iterator<NaryExistentialAxiomComposition> nextTransformed = null;
				
				@Override
				public boolean hasNext() {
					for (;;) {
						if (next != null) {
							return true;
						}
						
						if (nextTransformed != null) {
							while (nextTransformed.hasNext()) {
								NaryExistentialAxiomComposition candidate = nextTransformed.next();
								
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
						
						NaryExistentialAxiomComposition candidate = toDo.poll();
						
						if (candidate == null) {
							break;
						}
						//FIXME
						System.err.println("Rewriting " + candidate);
						
						// rewriting happens here
						nextTransformed = rewrite(candidate).iterator();
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
		
		// the property subsumption premise can have a derivation with lemmas. this method wraps with a special kind of expression
		// which produces lemma-free derivations.
		private AxiomExpression<ElkSubObjectPropertyOfAxiom> wrapSubPropertyExpression(AxiomExpression<ElkSubObjectPropertyOfAxiom> expr) {
			ElkSubObjectPropertyOfAxiom axiom = expr.getAxiom();
			IndexObjectConverter indexer = reader_.getIndexer();
			
			if (!axiom.getSubObjectPropertyExpression().accept(indexer).equals(axiom.getSuperObjectPropertyExpression().accept(indexer))) {
				return createSubPropertyExpression(expr);
			}
			
			return expr;
		}
		
		private SubPropertyChainExpression createSubPropertyExpression(AxiomExpression<ElkSubObjectPropertyOfAxiom> expr) {
			IndexObjectConverter indexer = reader_.getIndexer();
			IndexedObjectProperty superProperty = (IndexedObjectProperty) expr.getAxiom().getSuperObjectPropertyExpression().accept(indexer);
			IndexedPropertyChain subChain = expr.getAxiom().getSubObjectPropertyExpression().accept(indexer);
			
			return new SubPropertyChainExpression(expr, superProperty, subChain, reader_);
		}

		@Override
		public Iterable<Inference> visit(ExistentialComposition inf, Void input) {
			AxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = wrapSubPropertyExpression(inf.getSubPropertyPremise());
			
			if (propPremise == inf.getSubPropertyPremise()) {
				// the property premise is trivial, no need to wrap it
				return defaultVisit(inf, input);
			}
			
			return Collections.<Inference>singletonList(
					new ExistentialComposition(
							inf.getConclusion(), 
							inf.getSubsumerPremise(), 
							inf.getExistentialPremise(), 
							propPremise));
		}

		@Override
		public Iterable<Inference> visit(ExistentialChainAxiomComposition inf, Void input) {
			// check if there are premises not representable in OWL and transform to the n-ary inference if that's the case
			if (inf.getSecondExistentialPremise() instanceof LemmaExpression) {
				NaryExistentialAxiomComposition transformed = new NaryExistentialAxiomComposition(
															inf.getConclusion(), 
															Arrays.asList(inf.getFirstExistentialPremise(), inf.getSecondExistentialPremise()),
															// can have only chain axioms here. can't have transitivity, for example, that doesn't involve lemmas
															(AxiomExpression<ElkSubObjectPropertyOfAxiom>) inf.getChainPremise());
				
				return lazyLemmaElimination(transformed);
			}
			else {
				// wrapping the property subsumption premise, if needed
				AxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise = wrapSubPropertyExpression(inf.getFirstPropertyPremise());
				
				if (propPremise == inf.getFirstPropertyPremise()) {
					return defaultVisit(inf, input);
				}
				
				return Collections.<Inference>singletonList(
						new ExistentialChainAxiomComposition(
								inf.getConclusion(), 
								inf.getFirstExistentialPremise(), 
								inf.getSecondExistentialPremise(), 
								propPremise,
								inf.getSecondPropertyPremise(),
								inf.getChainPremise()));
			}
		}
		
		@Override
		public Iterable<Inference> visit(final NaryExistentialAxiomComposition inf, Void input) {
			if (!lemmasPresent(inf)) {
				return Collections.<Inference>singletonList(inf);
			}
			
			return lazyLemmaElimination(inf);
		}
		
		private Iterable<Inference> lazyLemmaElimination(final NaryExistentialAxiomComposition inf) {
			return new Iterable<Inference>() {

				@Override
				public Iterator<Inference> iterator() {
					return lazyEliminationIterator(inf);
				}
				
			};
		}

		public Iterable<NaryExistentialAxiomComposition> rewrite(final NaryExistentialAxiomComposition inf) {
			final List<Expression> commonPremises = new ArrayList<Expression>();
			
			for (int i = 0; i < inf.getExistentialPremises().size(); i++) {
				final int premiseIndex = i;
				Expression premise = inf.getExistentialPremises().get(i);
				
				if (premise instanceof LemmaExpression) {
					// replacing the current inference by a collection of inferences, one for each inference which derives the lemma premise
					List<NaryExistentialAxiomComposition> transformed = new LinkedList<NaryExistentialAxiomComposition>();
					
					try {
						for (Inference lemmaInf : premise.getInferences()) {
							transformed.add(lemmaInf.accept(new AbstractInferenceVisitor<Void, NaryExistentialAxiomComposition>() {

								@Override
								protected NaryExistentialAxiomComposition defaultVisit(Inference inference, Void input) {
									// shouldn't get here, check?
									return null;
								}

								@Override
								public NaryExistentialAxiomComposition visit(ExistentialLemmaChainComposition lemmaInf, Void input) {
									// only this inference can derive existential lemma premises
									List<Expression> premises = new ArrayList<Expression>(commonPremises);
									
									premises.add(lemmaInf.getFirstExistentialPremise());
									premises.add(lemmaInf.getSecondExistentialPremise());
									
									boolean moreLemmas = lemmaInf.getSecondExistentialPremise() instanceof LemmaExpression;
									// copying the remaining inferences
									for (int j = premiseIndex + 1; j < inf.getExistentialPremises().size(); j++) {
										Expression nextPremise = inf.getExistentialPremises().get(j);
										
										moreLemmas |= (nextPremise instanceof LemmaExpression);
										premises.add(nextPremise);
									}
									
									NaryExistentialAxiomComposition rewritten = null;
									
									if (!moreLemmas) {
										rewritten = recreateChainAxiom(
												inf.getConclusion(), 
												premises,
												inf.getChainPremise().getAxiom());
										//FIXME
										System.err.println("Lemma-free n-ary inference: " + rewritten);
									}
									else {
										rewritten = new NaryExistentialAxiomComposition(inf.getConclusion(), premises, inf.getChainPremise());	
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
			return Collections.emptyList();
		}

		private NaryExistentialAxiomComposition recreateChainAxiom(
				AxiomExpression<ElkSubClassOfAxiom> conclusion, 
				List<? extends Expression> existentialPremises,
				ElkSubObjectPropertyOfAxiom chainPremise) {
			List<ElkObjectPropertyExpression> chainList = new ArrayList<ElkObjectPropertyExpression>(existentialPremises.size());
			
			for (Expression premise : existentialPremises) {
				ElkSubClassOfAxiom exPremise = (ElkSubClassOfAxiom) ((AxiomExpression<?>) premise).getAxiom();
				ElkObjectSomeValuesFrom exSuper = (ElkObjectSomeValuesFrom) exPremise.getSuperClassExpression();
				
				chainList.add(exSuper.getProperty());
			}
			
			return new NaryExistentialAxiomComposition(
					conclusion,
					existentialPremises,
					// special expression for the chain premise
					new ChainRewritingExpression(
							getExpressionFactory().create(elkFactory_.getSubObjectPropertyOfAxiom(elkFactory_.getObjectPropertyChain(chainList), (ElkObjectProperty) chainPremise.getSuperObjectPropertyExpression())),
							chainPremise,
							reader_));
		}
		
	}


	@Override
	public boolean mayIntroduceDuplicates() {
		// rewriting existential binary composition inferences may produce
		// duplicate n-ary composition inferences
		return true;
	}
}
