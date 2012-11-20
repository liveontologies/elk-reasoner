package org.semanticweb.elk.util.collections.chains;
/*
 * #%L
 * ELK Utilities Collections
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

public interface ModifiableLink<T> {

	/**
	 * @return the object assigned in the reference or {@code null} if there is
	 *         no object assigned
	 */
	T next();

	/**
	 * Setting the reference to the given object. After that the {@link #next()}
	 * method should return this reference.
	 * 
	 * @param object
	 *            the object to which the reference is set
	 */
	void setNext(T object);

}
