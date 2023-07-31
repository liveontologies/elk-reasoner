package org.semanticweb.elk;

/*
 * #%L
 * ELK Common Utilities
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
 * A simple implementation of {@link ModifiableReference}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the value of this {@link Reference}
 */
public class ReferenceImpl<O> implements ModifiableReference<O> {

	private O object_;

	@Override
	public O get() {
		return object_;
	}

	@Override
	public void set(O object) {
		this.object_ = object;
	}
	
	@Override
	public String toString() {
		return String.valueOf(object_);
	}

}
