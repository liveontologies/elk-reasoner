package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1Watch;

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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;

public class ForwardLinkCompositionMatch1
		extends AbstractInferenceMatch<ForwardLinkComposition>
		implements BackwardLinkMatch1Watch {

	private final IndexedContextRootMatch destinationMatch_;

	private final ElkSubObjectPropertyExpression fullCompositionMatch_;

	private final int compositionStartPos_;

	ForwardLinkCompositionMatch1(ForwardLinkComposition parent,
			ForwardLinkMatch1 conclusionMatch) {
		super(parent);
		this.destinationMatch_ = conclusionMatch.getDestinationMatch();
		this.fullCompositionMatch_ = conclusionMatch.getFullChainMatch();
		this.compositionStartPos_ = conclusionMatch.getChainStartPos();
		checkEquals(conclusionMatch, getConclusionMatch(DEBUG_FACTORY));
	}

	IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public ElkSubObjectPropertyExpression getFullCompositionMatch() {
		return fullCompositionMatch_;
	}

	public int getCompositionStartPos() {
		return compositionStartPos_;
	}

	ForwardLinkMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getForwardLinkMatch1(getParent().getConclusion(factory),
				getDestinationMatch(), getFullCompositionMatch(),
				getCompositionStartPos());
	}

	ElkObjectPropertyExpression getFirstProperty() {
		final int startPos = getCompositionStartPos();
		// TODO: more rigorous matching checks
		return getFullCompositionMatch().accept(
				new ElkSubObjectPropertyExpressionVisitor<ElkObjectPropertyExpression>() {

					@Override
					public ElkObjectPropertyExpression visit(
							ElkObjectPropertyChain expression) {
						return expression.getObjectPropertyExpressions()
								.get(startPos);
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

	public BackwardLinkMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch1(
				getParent().getFirstPremise(factory), getDestinationMatch());
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

		O visit(ForwardLinkCompositionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ForwardLinkCompositionMatch1 getForwardLinkCompositionMatch1(
				ForwardLinkComposition parent,
				ForwardLinkMatch1 conclusionMatch);

	}

}
