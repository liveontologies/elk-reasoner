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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.inferences.AbstractInferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialChainAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialAxiomComposition;
import org.semanticweb.elk.proofs.inferences.classes.NaryExistentialLemmaComposition;
import org.semanticweb.elk.proofs.transformations.InferenceTransformation;
import org.semanticweb.elk.proofs.utils.TautologyChecker;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 * TODO
 * 
 * This transformation re-writes existential composition inferences into those which do not use derived sub-property chain axioms.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class LemmaElimination2 implements InferenceTransformation {

	private final ElkObjectFactory elkFactory_;
	
	private ReasonerInferenceReader reader_;
	
	public LemmaElimination2(ReasonerInferenceReader reader) {
		elkFactory_ = new ElkObjectFactoryImpl();
		reader_ = reader;
	}
	
	private DerivedExpressionFactory getExpressionFactory() {
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
			for (DerivedExpression premise : inf.getPremises()) {
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
		
		@Override
		public Iterable<Inference> visit(final ExistentialComposition inf, Void input) {
			ElkSubObjectPropertyOfAxiom propPremise = inf.getSubPropertyPremise().getAxiom();
			
			if (propPremise.accept(new TautologyChecker())) {
				return Collections.<Inference>singletonList(inf);
			}
			
			DerivedExpression exPremise = inf.getExistentialPremise().accept(new ExpressionVisitor<Void, DerivedExpression>() {

				@Override
				public DerivedExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr, Void input) {
					return createExistentialPremise((DerivedAxiomExpression<ElkSubClassOfAxiom>) expr, inf.getSubPropertyPremise());
				}

				@Override
				public DerivedExpression visit(LemmaExpression<?> expr, Void input) {
					return createExistentialPremise((LemmaExpression<ElkSubClassOfLemma>) expr, inf.getSubPropertyPremise());
				}
				
			}, null); 
			
			NaryExistentialAxiomComposition transformed = new NaryExistentialAxiomComposition(
					inf.getConclusion(), 
					Collections.singletonList(exPremise),
					inf.getSubPropertyPremise());

			return lazyLemmaElimination(transformed);
		}

		@Override
		public Iterable<Inference> visit(ExistentialChainAxiomComposition inf, Void input) {
			// first, replacing wrapping up the premises to unwind the chain hierarchy properly
			DerivedAxiomExpression<ElkSubClassOfAxiom> firstExPremise = createExistentialPremise(inf.getFirstExistentialPremise(), inf.getFirstPropertyPremise());
			List<DerivedExpression> otherExPremises = SuperChainExistential.createExistentialPremise(inf.getSecondExistentialPremise(), inf.getSecondPropertyPremise(), reader_);
			// second, check if there are premises not representable in OWL and transform to the n-ary inference if that's the case
			if (inf.getSecondExistentialPremise() instanceof LemmaExpression) {
				List<DerivedExpression> premises = new ArrayList<DerivedExpression>();
				
				premises.add(firstExPremise);
				premises.addAll(otherExPremises);
				
				NaryExistentialAxiomComposition transformed = new NaryExistentialAxiomComposition(
															inf.getConclusion(), 
															premises,
															// can have only chain axioms here. can't have transitivity, for example, that doesn't involve lemmas
															(DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) inf.getChainPremise());
				
				return lazyLemmaElimination(transformed);
			}
			else {
				DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> secondPropertyPremise = (DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom>) inf.getSecondPropertyPremise();
				DerivedAxiomExpression<ElkSubClassOfAxiom> secondExPremise = createExistentialPremise((DerivedAxiomExpression<ElkSubClassOfAxiom>) inf.getSecondExistentialPremise(), secondPropertyPremise);
				
				return Collections.<Inference>singletonList(
						new ExistentialChainAxiomComposition(
								inf.getConclusion(), 
								firstExPremise, 
								secondExPremise, 
								createTrivialPropertySubsumption((ElkObjectProperty) inf.getFirstPropertyPremise().getAxiom().getSubObjectPropertyExpression()),
								createTrivialPropertySubsumption((ElkObjectProperty) secondPropertyPremise.getAxiom().getSubObjectPropertyExpression()),
								inf.getChainPremise()));
			}
		}
		
		private DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> createTrivialPropertySubsumption(ElkObjectProperty prop) {
			return getExpressionFactory().create(elkFactory_.getSubObjectPropertyOfAxiom(prop, prop));
		}

		private DerivedAxiomExpression<ElkSubClassOfAxiom> createExistentialPremise(DerivedAxiomExpression<ElkSubClassOfAxiom> exPremise, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise) {
			ElkSubObjectPropertyOfAxiom propPremiseAxiom = propPremise.getAxiom();
			IndexObjectConverter indexer = reader_.getIndexer();
			IndexedPropertyChain sub = propPremiseAxiom.getSubObjectPropertyExpression().accept(indexer);
			IndexedPropertyChain sup = propPremiseAxiom.getSuperObjectPropertyExpression().accept(indexer);
			
			if (sub.equals(sup)) {
				return exPremise;
			}
			
			return new SuperPropertyExistential(exPremise, (IndexedObjectProperty) sub, (IndexedObjectProperty) sup, reader_);
		}
		
		private LemmaExpression<ElkSubClassOfLemma> createExistentialPremise(LemmaExpression<ElkSubClassOfLemma> exPremise, DerivedAxiomExpression<ElkSubObjectPropertyOfAxiom> propPremise) {
			ElkSubObjectPropertyOfAxiom propPremiseAxiom = propPremise.getAxiom();
			IndexObjectConverter indexer = reader_.getIndexer();
			IndexedPropertyChain sub = propPremiseAxiom.getSubObjectPropertyExpression().accept(indexer);
			IndexedPropertyChain sup = propPremiseAxiom.getSuperObjectPropertyExpression().accept(indexer);
			
			if (sub.equals(sup)) {
				return exPremise;
			}
			
			return new SuperChainExistential(exPremise, sub, sup, reader_);
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
			final List<DerivedExpression> commonPremises = new ArrayList<DerivedExpression>();
			
			for (int i = 0; i < inf.getExistentialPremises().size(); i++) {
				final int premiseIndex = i;
				DerivedExpression premise = inf.getExistentialPremises().get(i);
				
				if (premise instanceof LemmaExpression) {
					
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
								public NaryExistentialAxiomComposition visit(NaryExistentialAxiomComposition lemmaInf, Void input) {
									List<DerivedExpression> premises = new ArrayList<DerivedExpression>(commonPremises);
									
									premises.add(lemmaInf.getConclusion());
									
									// copying the remaining inferences
									for (int j = premiseIndex + 1; j < inf.getExistentialPremises().size(); j++) {
										DerivedExpression nextPremise = inf.getExistentialPremises().get(j);
										
										premises.add(nextPremise);
									}
									
									return new NaryExistentialAxiomComposition(inf.getConclusion(), premises, inf.getChainPremise());
								}
								
								@Override
								public NaryExistentialAxiomComposition visit(NaryExistentialLemmaComposition lemmaInf, Void input) {
									List<DerivedExpression> premises = new ArrayList<DerivedExpression>(commonPremises);
									
									premises.addAll(lemmaInf.getExistentialPremises());
									
									// copying the remaining inferences
									for (int j = premiseIndex + 1; j < inf.getExistentialPremises().size(); j++) {
										DerivedExpression nextPremise = inf.getExistentialPremises().get(j);
										
										premises.add(nextPremise);
									}
									
									return new NaryExistentialAxiomComposition(inf.getConclusion(), premises, inf.getChainPremise());
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
			
			return Collections.singletonList(inf);
		}
	}

	@Override
	public boolean mayIntroduceDuplicates() {
		return false;
	}
}
