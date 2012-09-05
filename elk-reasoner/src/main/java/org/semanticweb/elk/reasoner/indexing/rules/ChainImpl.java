package org.semanticweb.elk.reasoner.indexing.rules;
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

public class ChainImpl<T extends Chain<T>> implements Chain<T> {

	private T tail_ = null;

	public ChainImpl() {		
	}
	
	public ChainImpl(T tail) {
		this.tail_ = tail;
	}

	@Override
	public T getNext() {
		return tail_;
	}

	@Override
	public <S extends T> S find(ChainMatcher<T, S> matcher) {
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

	/** Warning: potentially unbounded recursion; use only with small chains! */
	@Override
	public <S extends T> S remove(ChainMatcher<T, S> matcher) {
		T candidate = tail_;
		if (candidate == null)
			return null;
		S match = matcher.match(candidate);
		if (match != null) {
			tail_ = candidate.getNext();
			return match;
		}
		return candidate.remove(matcher);
	}

	@Override
	public <S extends T> S getCreate(ChainMatcher<T, S> matcher) {
		S result = find(matcher);
		if (result != null)
			return result;
		result = matcher.createNew(tail_);
		tail_ = result;
		return result;
	}

}
