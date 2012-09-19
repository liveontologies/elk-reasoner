package org.semanticweb.elk.util.collections.chains;

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

/**
 * A simple implementation of the {@link Chain} interface. The methods are not
 * thread safe.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The types of elements in the chain.
 */
public class ChainImpl<T extends Reference<T>> extends AbstractChain<T>
		implements Chain<T> {

	/**
	 * the field to store the tail of the chain
	 */
	private T tail_ = null;

	public ChainImpl() {
	}

	public ChainImpl(T tail) {
		this.tail_ = tail;
	}

	@Override
	public T next() {
		return tail_;
	}

	@Override
	public void setNext(T tail) {
		tail_ = tail;
	}

}
