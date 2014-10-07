/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;

import java.util.Arrays;
import java.util.Collections;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExpressionMapper {

	public static Iterable<TracingInput> convertExpressionToTracingInputs(
			Expression expression, IndexObjectConverter converter) {
		return expression.accept(new Converter(converter), null);
	}

	private static class Converter extends
			AbstractElkAxiomVisitor<Iterable<TracingInput>> implements
			ExpressionVisitor<Void, Iterable<TracingInput>>,
			ElkLemmaVisitor<Void, Iterable<TracingInput>> {

		private final IndexObjectConverter converter_;

		public Converter(IndexObjectConverter c) {
			converter_ = c;
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

						@Override
						public Conclusion visit(ElkClassExpressionWrap ce,
								Void input) {
							return new DecomposedSubsumerImpl<IndexedClassExpression>(
									ce.getClassExpression().accept(converter_));
						}

					}, input);

			return singleton(new ClassTracingInput(subsumee, subsumer));
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
		public Iterable<TracingInput> visit(DerivedAxiomExpression expr, Void input) {
			return expr.getAxiom().accept(this);
		}

		@Override
		public Iterable<TracingInput> visit(DerivedLemmaExpression expr, Void input) {
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
			
			if (subsumer instanceof IndexedObjectSomeValuesFrom) {
				IndexedObjectSomeValuesFrom existential = (IndexedObjectSomeValuesFrom) subsumer;
				/*
				 * This expression can also represent a backward link so we
				 * create that input, too
				 */
				TracingInput linkInput = new ClassTracingInput(existential.getFiller(), new BackwardLinkImpl(subsumee, existential.getRelation()));
				
				return Arrays.asList(subsumptionInput, linkInput);
			}
			else {
				return singleton(subsumptionInput); 
			}
		}
		
		private Iterable<TracingInput> singleton(TracingInput input) {
			return Collections.singletonList(input);
		}

	}

}
