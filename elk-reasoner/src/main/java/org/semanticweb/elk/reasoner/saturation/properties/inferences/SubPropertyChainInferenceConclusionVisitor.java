package org.semanticweb.elk.reasoner.saturation.properties.inferences;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * A {@link SubPropertyChainInference.Visitor} that processes all conclusions of
 * the visited {@link SubPropertyChainInference}s using the provided
 * {@code SubPropertyChain.Visitor}.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 */
public class SubPropertyChainInferenceConclusionVisitor<O>
		implements
			SubPropertyChainInference.Visitor<O> {

	private final SubPropertyChain.Visitor<O> conclusionVisitor_;

	public SubPropertyChainInferenceConclusionVisitor(
			SubPropertyChain.Visitor<O> conclusionVisitor) {
		this.conclusionVisitor_ = conclusionVisitor;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

}
