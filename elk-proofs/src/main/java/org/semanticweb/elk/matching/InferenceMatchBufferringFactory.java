package org.semanticweb.elk.matching;

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

import java.util.Collection;
import java.util.Queue;

import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.InferenceMatchBaseFactory;
import org.semanticweb.elk.matching.inferences.InferenceMatchDelegatingFactory;
import org.semanticweb.elk.matching.inferences.InferenceMatch.Factory;

class InferenceMatchBufferringFactory extends InferenceMatchDelegatingFactory {

	private final Collection<InferenceMatch> newInferences_;

	InferenceMatchBufferringFactory(Factory mainFactory,
			Collection<InferenceMatch> newInferences) {
		super(mainFactory);
		this.newInferences_ = newInferences;
	}

	InferenceMatchBufferringFactory(Queue<InferenceMatch> toDo) {
		this(new InferenceMatchBaseFactory(), toDo);
	}

	@Override
	protected <T extends InferenceMatch> T filter(T match) {
		newInferences_.add(match);
		return match;
	}

}
