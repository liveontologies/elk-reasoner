/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;
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

import java.util.Arrays;
import java.util.Collections;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.AbstractElkClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;

/**
 * Converts {@link Expression}s to instances of {@link TracingInput} which can be used to launch tracing. 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExpressionMapper {
	
	private final EntailmentChecker entailmentChecker_;
	
	private final IndexObjectConverter indexer_;
	
	public ExpressionMapper(IndexObjectConverter indexer, EntailmentChecker checker) {
		entailmentChecker_ = checker;
		indexer_ = indexer;
	}

	public Iterable<TracingInput> convertExpressionToTracingInputs(Expression expression) {
		return expression.accept(new Converter(), null);
	}

	private class Converter extends
			AbstractElkAxiomVisitor<Iterable<TracingInput>> implements
			ExpressionVisitor<Void, Iterable<TracingInput>>,
			ElkLemmaVisitor<Void, Iterable<TracingInput>> {

		@Override
		protected Iterable<TracingInput> defaultLogicalVisit(ElkAxiom axiom) {
			// can only get here if the axiom stands for an expression which can
			// never be derived, e.g. an EquivalentClasses axiom.
			return Collections.emptyList();
		}

		@Override
		public Iterable<TracingInput> visit(ElkReflexivePropertyChainLemma lemma,
				Void input) {
			IndexedPropertyChain chain = lemma.getPropertyChain().accept(
					indexer_);

			return singleton(new ObjectPropertyTracingInput(
					new ReflexivePropertyChain<IndexedPropertyChain>(chain)));
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubClassOfLemma lemma, Void input) {
			IndexedClassExpression subsumee = lemma.getSubClass().accept(indexer_);
			Conclusion subsumer = lemma.getSuperClass().accept(
					new ElkComplexClassExpressionVisitor<Void, Conclusion>() {

						@Override
						public Conclusion visit(
								ElkComplexObjectSomeValuesFrom ce, Void input) {
							// a forward link
							IndexedPropertyChain chain = ce.getPropertyChain()
									.accept(indexer_);
							IndexedClassExpression filler = ce.getFiller()
									.accept(indexer_);

							return new ForwardLinkImpl(chain, filler);
						}

					}, input);

			if (entailmentChecker_.isSatisfiable(subsumee)) {
				return singleton(new ClassTracingInput(subsumee, subsumer));
			}
			else {
				return Arrays.<TracingInput>asList(new ClassTracingInput(subsumee, subsumer), new ClassTracingInput(subsumee, ContradictionImpl.getInstance()));
			}
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubPropertyChainOfLemma lemma, Void input) {
			IndexedPropertyChain subchain = lemma.getSubPropertyChain().accept(
					indexer_);
			IndexedPropertyChain superchain = lemma.getSuperPropertyChain()
					.accept(indexer_);

			return singleton(new ObjectPropertyTracingInput(
					new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(
							subchain, superchain)));
		}

		@Override
		public Iterable<TracingInput> visit(DerivedAxiomExpression<?> expr, Void input) {
			return expr.getAxiom().accept(this);
		}

		@Override
		public Iterable<TracingInput> visit(LemmaExpression<?> expr, Void input) {
			return expr.getLemma().accept(this, input);
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubClassOfAxiom ax) {
			IndexedClassExpression subsumee = getIndexedSubClassOf(ax.getSubClassExpression());
			IndexedClassExpression subsumer = ax.getSuperClassExpression().accept(indexer_);
			Conclusion subsumerConclusion = new DecomposedSubsumerImpl<IndexedClassExpression>(subsumer);
			TracingInput subsumptionInput = new ClassTracingInput(subsumee, subsumerConclusion);
			
			subsumptionInput = !entailmentChecker_.isDerivedSubsumer(subsumee, subsumer) && !entailmentChecker_.isSatisfiable(subsumee)
					? new ClassTracingInput(subsumee, ContradictionImpl.getInstance()) 
					: subsumptionInput;
			
			if (subsumer instanceof IndexedObjectSomeValuesFrom) {
				IndexedObjectSomeValuesFrom existential = (IndexedObjectSomeValuesFrom) subsumer;
				 //This expression can also represent a backward link so we create that input, too
				TracingInput linkInput = new ClassTracingInput(existential.getFiller(), new BackwardLinkImpl(subsumee, existential.getRelation()));
				
				return Arrays.asList(subsumptionInput, linkInput);
			}
			else {
				return singleton(subsumptionInput); 
			}
		}
		
		private IndexedClassExpression getIndexedSubClassOf(ElkClassExpression ce) {
			return ce.accept(new AbstractElkClassExpressionVisitor<IndexedClassExpression>() {

				@Override
				protected IndexedClassExpression defaultVisit(ElkClassExpression ce) {
					return ce.accept(indexer_);
				}

				@Override
				public IndexedClassExpression visit(ElkObjectOneOf ce) {
					return ce.getIndividuals().size() == 1 ? ce.getIndividuals().get(0).accept(indexer_) : defaultVisit(ce);
				}
				
			});
		}

		@Override
		public Iterable<TracingInput> visit(ElkClassAssertionAxiom ax) {
			IndexedClassExpression subsumee = ax.getIndividual().accept(indexer_);
			IndexedClassExpression subsumer = ax.getClassExpression().accept(indexer_);
			
			TracingInput input = !entailmentChecker_.isDerivedSubsumer(subsumee, subsumer) && !entailmentChecker_.isSatisfiable(subsumee) 
					? new ClassTracingInput(subsumee, new DecomposedSubsumerImpl<IndexedClassExpression>(subsumer))
					: new ClassTracingInput(subsumee, ContradictionImpl.getInstance());
			
			return Collections.singleton(input);
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubObjectPropertyOfAxiom ax) {
			IndexedPropertyChain subchain = ax.getSubObjectPropertyExpression().accept(indexer_);
			IndexedPropertyChain sup = ax.getSuperObjectPropertyExpression().accept(indexer_);

			return singleton(new ObjectPropertyTracingInput(
					new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(subchain, sup)));
		}

		@Override
		public Iterable<TracingInput> visit(ElkReflexiveObjectPropertyAxiom ax) {
			IndexedObjectProperty p = (IndexedObjectProperty) ax.getProperty().accept(indexer_);
			
			return singleton(new ObjectPropertyTracingInput(new ToldReflexiveProperty(p)));
		}

		private Iterable<TracingInput> singleton(TracingInput input) {
			return Collections.singletonList(input);
		}

	}

}
