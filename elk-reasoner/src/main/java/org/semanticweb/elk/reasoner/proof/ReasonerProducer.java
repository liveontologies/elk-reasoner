package org.semanticweb.elk.reasoner.proof;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2024 Department of Computer Science, University of Oxford
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

/**
 * A general interface for object through which objects of a particular types
 * can be produced
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the types of objects that can be produced by this
 *            {@link ReasonerProducer}
 */
public interface ReasonerProducer<O> {

	void produce(O object);

	static class Dummy<O> implements ReasonerProducer<O> {

		@Override
		public void produce(O object) {
			// no-op
		}

	}

	static ReasonerProducer<Object> DUMMY = new Dummy<Object>();

	@SuppressWarnings("unchecked")
	public static <O> ReasonerProducer<O> dummy() {
		return (ReasonerProducer<O>) DUMMY;
	}

}
