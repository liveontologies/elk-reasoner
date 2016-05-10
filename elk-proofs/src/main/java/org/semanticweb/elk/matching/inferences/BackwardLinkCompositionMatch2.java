package org.semanticweb.elk.matching.inferences;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

public class BackwardLinkCompositionMatch2
		extends AbstractInferenceMatch<BackwardLinkCompositionMatch1>
		implements BackwardLinkMatch1Watch {

	private final ElkSubObjectPropertyExpression compositionMatch_;
	private final ElkObjectProperty conclusionRelationMatch_;

	BackwardLinkCompositionMatch2(BackwardLinkCompositionMatch1 parent,
			IndexedSubObjectPropertyOfAxiomMatch2 fifthPremiseMatch) {
		super(parent);
		this.compositionMatch_ = fifthPremiseMatch.getSubPropertyChainMatch();
		this.conclusionRelationMatch_ = fifthPremiseMatch
				.getSuperPropertyMatch();
	}

	public ElkSubObjectPropertyExpression getCompositionMatch() {
		return compositionMatch_;
	}

	public ElkObjectProperty getConclusionRelationMatch() {
		return conclusionRelationMatch_;
	}

	ElkObjectPropertyExpression getFirstProperty() {
		return getCompositionMatch().accept(
				new ElkSubObjectPropertyExpressionVisitor<ElkObjectPropertyExpression>() {

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectPropertyChain expression) {
						return expression.getObjectPropertyExpressions().get(0);
					}

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectInverseOf expression) {
						return expression;
					}

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectProperty expression) {
						return expression;
					}
				});
	}

	public IndexedSubObjectPropertyOfAxiomMatch2 getFifthPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedSubObjectPropertyOfAxiomMatch2(
				getParent().getFifthPremiseMatch(factory),
				getCompositionMatch(), getConclusionRelationMatch());

	}

	public BackwardLinkMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch1(
				getParent().getParent().getFirstPremise(factory),
				getParent().getConclusionSourceMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(BackwardLinkMatch1Watch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> {

		O visit(BackwardLinkCompositionMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		BackwardLinkCompositionMatch2 getBackwardLinkCompositionMatch2(
				BackwardLinkCompositionMatch1 parent,
				IndexedSubObjectPropertyOfAxiomMatch2 fifthPremiseMatch);

	}

}
