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
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;

public class SubPropertyChainExpandedSubObjectPropertyOfMatch3 extends
		AbstractInferenceMatch<SubPropertyChainExpandedSubObjectPropertyOfMatch2> {

	SubPropertyChainExpandedSubObjectPropertyOfMatch3(
			SubPropertyChainExpandedSubObjectPropertyOfMatch2 parent,
			SubPropertyChainMatch2 secondPremiseMatch) {
		super(parent);
		checkEquals(secondPremiseMatch, getSecondPremiseMatch(DEBUG_FACTORY));
	}

	SubPropertyChainMatch2 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch2(
				getParent().getSecondPremiseMatch(factory),
				getParent().getInterPropertyMatch(), 0);
	}

	public SubPropertyChainMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubPropertyChainMatch2(
				getParent().getParent().getConclusionMatch(factory),
				getParent().getSubChainMatch(), 0);
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

		O visit(SubPropertyChainExpandedSubObjectPropertyOfMatch3 inferenceMatch3);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubPropertyChainExpandedSubObjectPropertyOfMatch3 getSubPropertyChainExpandedSubObjectPropertyOfMatch3(
				SubPropertyChainExpandedSubObjectPropertyOfMatch2 parent,
				SubPropertyChainMatch2 secondPremiseMatch);

	}

}
