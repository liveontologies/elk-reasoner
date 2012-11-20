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
 * A simple implementation of the {@link Link} and {@link ModifiableLink}
 * interfaces. The methods are not thread safe.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The types of elements in the chain.
 */
public class ModifiableLinkImpl<T> implements ModifiableLink<T> {

	/**
	 * the field to store the next element
	 */
	private T next_ = null;

	public ModifiableLinkImpl() {
	}

	public ModifiableLinkImpl(T next) {
		this.next_ = next;
	}

	@Override
	public T next() {
		return next_;
	}

	@Override
	public void setNext(T next) {
		next_ = next;
	}

}
