package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

public abstract class AbstractChain<T extends Reference<T>> implements Chain<T> {

	@Override
	public abstract T getNext();

	@Override
	public abstract void setNext(T tail);

	@Override
	public <S extends T> S find(Matcher<T, S> matcher) {
		T candidate = getNext();
		for (;;) {
			if (candidate == null)
				return null;
			S match = matcher.match(candidate);
			if (match != null)
				return match;
			candidate = candidate.getNext();
		}
	}

	@Override
	public <S extends T> S getCreate(Matcher<T, S> matcher) {
		T candidate = getNext();
		for (;;) {
			if (candidate == null) {
				S result = matcher.create(getNext());
				setNext(result);
				return result;
			}
			S match = matcher.match(candidate);
			if (match != null)
				return match;
			candidate = candidate.getNext();
		}
	}

	@Override
	public <S extends T> S remove(Matcher<T, S> matcher) {
		Reference<T> point = this;
		for (;;) {
			T next = point.getNext();
			if (next == null)
				return null;
			S match = matcher.match(next);
			if (match != null) {
				point.setNext(next.getNext());
				return match;
			}
			point = next;
		}
	}

}
