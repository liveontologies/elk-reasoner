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
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;

public class SubPropertyChainExpandedSubObjectPropertyOfMatch1 extends
		AbstractInferenceMatch<SubPropertyChainExpandedSubObjectPropertyOf>
		implements IndexedSubObjectPropertyOfAxiomMatch1Watch {

	private final ElkSubObjectPropertyExpression fullSuperChainMatch_;

	private final int superChainStartPos_;

	SubPropertyChainExpandedSubObjectPropertyOfMatch1(
			SubPropertyChainExpandedSubObjectPropertyOf parent,
			SubPropertyChainMatch1 conclusionMatch) {
		super(parent);
		fullSuperChainMatch_ = conclusionMatch.getFullSuperChainMatch();
		superChainStartPos_ = conclusionMatch.getSuperChainStartPos();
	}

	public ElkSubObjectPropertyExpression getFullSuperChainMatch() {
		return fullSuperChainMatch_;
	}

	public int getSuperChainStartPos() {
		return superChainStartPos_;
	}

	public SubPropertyChainMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch1(
				getParent().getConclusion(factory), fullSuperChainMatch_,
				superChainStartPos_);

	}

	public IndexedSubObjectPropertyOfAxiomMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedSubObjectPropertyOfAxiomMatch1(
				getParent().getFirstPremise(factory));
	}

	public SubPropertyChainMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch1(
				getParent().getSecondPremise(factory), fullSuperChainMatch_,
				superChainStartPos_);
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(
			IndexedSubObjectPropertyOfAxiomMatch1Watch.Visitor<O> visitor) {
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

		O visit(SubPropertyChainExpandedSubObjectPropertyOfMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubPropertyChainExpandedSubObjectPropertyOfMatch1 getSubPropertyChainExpandedSubObjectPropertyOfMatch1(
				SubPropertyChainExpandedSubObjectPropertyOf parent,
				SubPropertyChainMatch1 conclusionMatch);

	}

}
