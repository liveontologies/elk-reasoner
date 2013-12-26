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

/**
 * A factoring for creating references
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of elements to which a reference should be created
 * @param <O>
 *            the type of the output references
 */
public interface ReferenceFactory<T, O> {

	/**
	 * Creates a references to the given object
	 * 
	 * @param object
	 * @return the reference to the given object
	 */
	O create(T object);

}
