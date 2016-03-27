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
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;

public class SubPropertyChainTautologyMatch1
		extends AbstractInferenceMatch<SubPropertyChainTautology> {

	private final ElkSubObjectPropertyExpression fullChainMatch_;
	private final int chainStartPos_;

	SubPropertyChainTautologyMatch1(SubPropertyChainTautology parent,
			SubPropertyChainMatch1 conclusionMatch) {
		super(parent);
		fullChainMatch_ = conclusionMatch.getFullSuperChainMatch();
		chainStartPos_ = conclusionMatch.getSuperChainStartPos();
	}

	public ElkSubObjectPropertyExpression getFullChainMatch() {
		return fullChainMatch_;
	}

	public int getChainStartPos() {
		return chainStartPos_;
	}

	public SubPropertyChainMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory
				.getSubPropertyChainMatch2(
						factory.getSubPropertyChainMatch1(
								getParent().getConclusion(factory),
								fullChainMatch_, chainStartPos_),
						fullChainMatch_, chainStartPos_);
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

		O visit(SubPropertyChainTautologyMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubPropertyChainTautologyMatch1 getSubPropertyChainTautologyMatch1(
				SubPropertyChainTautology parent,
				SubPropertyChainMatch1 conclusionMatch);

	}

}
