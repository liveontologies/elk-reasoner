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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

public class PropertyRangeInheritedMatch2
		extends AbstractInferenceMatch<PropertyRangeInheritedMatch1>
		implements SubPropertyChainMatch1Watch {

	private final ElkObjectProperty superPropertyMatch_;

	private final ElkClassExpression rangeMatch_;

	PropertyRangeInheritedMatch2(PropertyRangeInheritedMatch1 parent,
			IndexedObjectPropertyRangeAxiomMatch2 secondPremiseMatch) {
		super(parent);
		this.superPropertyMatch_ = secondPremiseMatch.getPropertyMatch();
		this.rangeMatch_ = secondPremiseMatch.getRangeMatch();
		checkEquals(secondPremiseMatch, getSecondPremiseMatch(DEBUG_FACTORY));
	}

	public ElkObjectProperty getSuperPropertyMatch() {
		return superPropertyMatch_;
	}

	public ElkClassExpression getRangeMatch() {
		return rangeMatch_;
	}

	public SubPropertyChainMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch1(
				getParent().getParent().getFirstPremise(factory),
				superPropertyMatch_, 0);
	}

	IndexedObjectPropertyRangeAxiomMatch2 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedObjectPropertyRangeAxiomMatch2(
				getParent().getSecondPremiseMatch(factory),
				getSuperPropertyMatch(), getRangeMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(SubPropertyChainMatch1Watch.Visitor<O> visitor) {
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

		O visit(PropertyRangeInheritedMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		PropertyRangeInheritedMatch2 getPropertyRangeInheritedMatch2(
				PropertyRangeInheritedMatch1 parent,
				IndexedObjectPropertyRangeAxiomMatch2 secondPremiseMatch);

	}

}
