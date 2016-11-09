package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.matching.ElkMatchException;

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
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

public class PropertyRangeInheritedMatch3
		extends AbstractInferenceMatch<PropertyRangeInheritedMatch2> {

	private final ElkObjectProperty subPropertyMatch_;

	PropertyRangeInheritedMatch3(PropertyRangeInheritedMatch2 parent,
			SubPropertyChainMatch2 firstPremiseMatch) {
		super(parent);
		ElkSubObjectPropertyExpression fullSubChainMatch = firstPremiseMatch
				.getFullSubChainMatch();
		int subChainStartPos = firstPremiseMatch.getSubChainStartPos();
		if (fullSubChainMatch instanceof ElkObjectProperty
				&& subChainStartPos == 0) {
			this.subPropertyMatch_ = (ElkObjectProperty) fullSubChainMatch;
		} else {
			throw new ElkMatchException(
					parent.getParent().getParent().getSubProperty(),
					fullSubChainMatch, subChainStartPos);
		}
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public ElkObjectProperty getSubPropertyMatch() {
		return subPropertyMatch_;
	}

	SubPropertyChainMatch2 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch2(
				getParent().getFirstPremiseMatch(factory),
				getSubPropertyMatch(), 0);
	}

	public PropertyRangeMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropertyRangeMatch2(
				getParent().getParent().getConclusionMatch(factory),
				getSubPropertyMatch(), getParent().getRangeMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
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

		O visit(PropertyRangeInheritedMatch3 inferenceMatch3);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		PropertyRangeInheritedMatch3 getPropertyRangeInheritedMatch3(
				PropertyRangeInheritedMatch2 parent,
				SubPropertyChainMatch2 firstPremiseMatch);

	}

}
