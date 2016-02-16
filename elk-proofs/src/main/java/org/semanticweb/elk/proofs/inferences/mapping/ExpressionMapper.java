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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.AbstractElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.proofs.expressions.AxiomExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.LemmaExpression;
import org.semanticweb.elk.proofs.expressions.entries.StructuralEquivalenceChecker;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassInconsistencyImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SubClassInclusionDecomposedImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SubPropertyChainImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ReflexivePropertyChainImpl;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ToldReflexiveProperty;

/**
 * Converts {@link Expression}s to instances of {@link TracingInput} which can
 * be used to launch tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ExpressionMapper {

	private final ElkPolarityExpressionConverter converter_;
	private final ElkSubObjectPropertyExpressionVisitor<? extends IndexedPropertyChain> propertyChainConverter_;

	public ExpressionMapper(
			ElkPolarityExpressionConverter converter,
			ElkSubObjectPropertyExpressionVisitor<? extends IndexedPropertyChain> propertyChainConverter) {
		this.converter_ = converter;
		this.propertyChainConverter_ = propertyChainConverter;
	}

	public Iterable<TracingInput> convertExpressionToTracingInputs(
			Expression expression) {
		return expression.accept(new Converter(), null);
	}

	private class Converter extends
			DummyElkAxiomVisitor<Iterable<TracingInput>> implements
			ExpressionVisitor<Void, Iterable<TracingInput>>,
			ElkLemmaVisitor<Void, Iterable<TracingInput>> {

		@Override
		protected Iterable<TracingInput> defaultLogicalVisit(ElkAxiom axiom) {
			// can only get here if the axiom stands for an expression which can
			// never be derived, e.g. an EquivalentClasses axiom.
			return Collections.emptyList();
		}

		@Override
		public Iterable<TracingInput> visit(
				ElkReflexivePropertyChainLemma lemma, Void input) {
			IndexedPropertyChain chain = lemma.getPropertyChain().accept(
					propertyChainConverter_);

			return singleton(new ObjectPropertyTracingInput(
					new ReflexivePropertyChainImpl<IndexedPropertyChain>(chain)));
		}

		@Override
		public Iterable<TracingInput> visit(final ElkSubClassOfLemma lemma,
				Void input) {
			ClassConclusion subsumption = lemma.getSuperClass().accept(
					new ElkComplexClassExpressionVisitor<Void, ClassConclusion>() {

						@Override
						public ClassConclusion visit(
								ElkComplexObjectSomeValuesFrom ce, Void input) {
							// a forward link
							IndexedPropertyChain chain = ce.getPropertyChain()
									.accept(propertyChainConverter_);
							IndexedClassExpression filler = ce.getFiller()
									.accept(converter_);

							return new ForwardLinkImpl<IndexedPropertyChain>(
									lemma.getSubClass().accept(converter_),
									chain, filler);
						}

					}, input);

			return singleton(new ClassTracingInput(subsumption));

		}

		@Override
		public Iterable<TracingInput> visit(ElkSubPropertyChainOfLemma lemma,
				Void input) {
			IndexedPropertyChain subchain = lemma.getSubPropertyChain().accept(
					propertyChainConverter_);
			IndexedPropertyChain superchain = lemma.getSuperPropertyChain()
					.accept(propertyChainConverter_);

			return singleton(new ObjectPropertyTracingInput(
					new SubPropertyChainImpl<IndexedPropertyChain, IndexedPropertyChain>(
							subchain, superchain)));
		}

		@Override
		public Iterable<TracingInput> visit(AxiomExpression<?> expr, Void input) {
			return expr.getAxiom().accept(this);
		}

		@Override
		public Iterable<TracingInput> visit(LemmaExpression<?> expr, Void input) {
			return expr.getLemma().accept(this, input);
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubClassOfAxiom ax) {
			IndexedClassExpression subsumee = getIndexedSubClassOf(ax
					.getSubClassExpression());
			ElkClassExpression sup = ax.getSuperClassExpression();
			List<TracingInput> result = new ArrayList<TracingInput>(2);
			if (sup == PredefinedElkClass.OWL_NOTHING) {
				result.add(new ClassTracingInput(
						new ClassInconsistencyImpl(subsumee)));
				return result;
			}
			// else
			IndexedClassExpression subsumer = sup.accept(converter_);
			if (subsumer != null) {
				result.add(new ClassTracingInput(
						new SubClassInclusionDecomposedImpl<IndexedClassExpression>(
								subsumee, subsumer)));
			}

			if (sup instanceof ElkObjectSomeValuesFrom) {
				ElkObjectSomeValuesFrom existential = (ElkObjectSomeValuesFrom) sup;
				// This expression can also represent a backward link so we
				// create that input, too
				result.add(new ClassTracingInput(new BackwardLinkImpl(
						existential.getFiller().accept(converter_), existential
								.getProperty().accept(converter_), subsumee)));
			}

			return result;
		}

		private IndexedClassExpression getIndexedSubClassOf(
				ElkClassExpression ce) {
			return ce
					.accept(new AbstractElkClassExpressionVisitor<IndexedClassExpression>() {

						@Override
						protected IndexedClassExpression defaultVisit(
								ElkClassExpression ce) {
							return ce.accept(converter_);
						}

						@Override
						public IndexedClassExpression visit(ElkObjectOneOf ce) {
							return ce.getIndividuals().size() == 1 ? ce
									.getIndividuals().get(0).accept(converter_)
									: defaultVisit(ce);
						}

					});
		}

		@Override
		public Iterable<TracingInput> visit(ElkClassAssertionAxiom ax) {
			IndexedClassExpression subsumee = ax.getIndividual().accept(
					converter_);
			IndexedClassExpression subsumer = ax.getClassExpression().accept(
					converter_);

			ClassConclusion conclusion = isNothing(ax.getClassExpression()) ? new ClassInconsistencyImpl(
					subsumee)
					: new SubClassInclusionDecomposedImpl<IndexedClassExpression>(
							subsumee, subsumer);
			TracingInput input = new ClassTracingInput(conclusion);

			return Collections.singleton(input);
		}

		@Override
		public Iterable<TracingInput> visit(ElkSubObjectPropertyOfAxiom ax) {
			IndexedPropertyChain subchain = ax.getSubObjectPropertyExpression()
					.accept(propertyChainConverter_);
			IndexedPropertyChain sup = ax.getSuperObjectPropertyExpression()
					.accept(converter_);

			return singleton(new ObjectPropertyTracingInput(
					new SubPropertyChainImpl<IndexedPropertyChain, IndexedPropertyChain>(
							subchain, sup)));
		}

		@Override
		public Iterable<TracingInput> visit(ElkReflexiveObjectPropertyAxiom ax) {
			IndexedObjectProperty p = (IndexedObjectProperty) ax.getProperty()
					.accept(converter_);

			return singleton(new ObjectPropertyTracingInput(
					new ToldReflexiveProperty(p, ax)));
		}

		private Iterable<TracingInput> singleton(TracingInput input) {
			return Collections.singletonList(input);
		}

		private boolean isNothing(ElkClassExpression ce) {
			return StructuralEquivalenceChecker.equal(ce,
					PredefinedElkClass.OWL_NOTHING);
		}

	}

}
