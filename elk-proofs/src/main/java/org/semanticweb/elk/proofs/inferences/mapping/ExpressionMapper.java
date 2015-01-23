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
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
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

	public static Iterable<TracingInput> convertExpressionToTracingInputs(
			Expression expression, IndexObjectConverter converter, SatisfiabilityChecker checker) {
		return expression.accept(new Converter(converter, checker), null);
	}

	private static class Converter extends
			AbstractElkAxiomVisitor<Iterable<TracingInput>> implements
			ExpressionVisitor<Void, Iterable<TracingInput>>,
			ElkLemmaVisitor<Void, Iterable<TracingInput>> {

		private final IndexObjectConverter converter_;
		
		private final SatisfiabilityChecker checker_;

		public Converter(IndexObjectConverter c, SatisfiabilityChecker checker) {
			converter_ = c;
			checker_ = checker;
		}
		
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
					converter_);

			return singleton(new ObjectPropertyTracingInput(
					new ReflexivePropertyChain<IndexedPropertyChain>(chain)));
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubClassOfLemma lemma, Void input) {
			IndexedClassExpression subsumee = lemma.getSubClass().accept(converter_);
			Conclusion subsumer = lemma.getSuperClass().accept(
					new ElkComplexClassExpressionVisitor<Void, Conclusion>() {

						@Override
						public Conclusion visit(
								ElkComplexObjectSomeValuesFrom ce, Void input) {
							// a forward link
							IndexedPropertyChain chain = ce.getPropertyChain()
									.accept(converter_);
							IndexedClassExpression filler = ce.getFiller()
									.accept(converter_);

							return new ForwardLinkImpl(chain, filler);
						}

					}, input);

			if (checker_.isSatisfiable(subsumee)) {
				return singleton(new ClassTracingInput(subsumee, subsumer));
			}
			else {
				return Arrays.<TracingInput>asList(new ClassTracingInput(subsumee, subsumer), new ClassTracingInput(subsumee, ContradictionImpl.getInstance()));
			}
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubPropertyChainOfLemma lemma, Void input) {
			IndexedPropertyChain subchain = lemma.getSubPropertyChain().accept(
					converter_);
			IndexedPropertyChain superchain = lemma.getSuperPropertyChain()
					.accept(converter_);

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
			IndexedClassExpression subsumee = ax.getSubClassExpression()
					.accept(converter_);
			IndexedClassExpression subsumer = ax.getSuperClassExpression()
					.accept(converter_);
			
			TracingInput subsumptionInput = new ClassTracingInput(
					subsumee,
					new DecomposedSubsumerImpl<IndexedClassExpression>(subsumer));
			
			TracingInput contradictionInput = checker_.isSatisfiable(subsumee) ? null : new ClassTracingInput(subsumee, ContradictionImpl.getInstance());
			
			if (subsumer instanceof IndexedObjectSomeValuesFrom) {
				IndexedObjectSomeValuesFrom existential = (IndexedObjectSomeValuesFrom) subsumer;
				 //This expression can also represent a backward link so we create that input, too
				TracingInput linkInput = new ClassTracingInput(existential.getFiller(), new BackwardLinkImpl(subsumee, existential.getRelation()));
				
				return contradictionInput == null ? Arrays.asList(subsumptionInput, linkInput) : Arrays.asList(subsumptionInput, linkInput, contradictionInput);
			}
			else {
				return contradictionInput == null ? singleton(subsumptionInput) : Arrays.asList(contradictionInput, subsumptionInput); 
			}
		}
		
		@Override
		public Iterable<TracingInput> visit(ElkSubObjectPropertyOfAxiom ax) {
			IndexedPropertyChain subchain = ax.getSubObjectPropertyExpression().accept(converter_);
			IndexedPropertyChain sup = ax.getSuperObjectPropertyExpression().accept(converter_);

			return singleton(new ObjectPropertyTracingInput(
					new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(subchain, sup)));
		}

		@Override
		public Iterable<TracingInput> visit(ElkReflexiveObjectPropertyAxiom ax) {
			IndexedObjectProperty p = (IndexedObjectProperty) ax.getProperty().accept(converter_);
			
			return singleton(new ObjectPropertyTracingInput(new ToldReflexiveProperty(p)));
		}

		private Iterable<TracingInput> singleton(TracingInput input) {
			return Collections.singletonList(input);
		}

	}

}
