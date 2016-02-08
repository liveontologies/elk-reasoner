package org.semanticweb.elk.reasoner.taxonomy.model;

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

/**
 * Instances of this interface are able to return keys of objects of type <code>T</code>.
 * The purpose of these keys is that the methods {@link #hashCode()} and
 * {@link #equals(Object)} will be called on them instead of the original objects.
 * 
 * @author Peter Skocovsky
 *
 * @param <T> The type of the objects for which the keys are provided.
 */
public interface KeyProvider<T> {
	
	/**
	 * Returns the key for <code>arg</code>. The methods {@link #hashCode()} and
	 * {@link #equals(Object)} will be called on this key instead of the
	 * original object <code>arg</code>.
	 * 
	 * @param arg The object for which the key should be returned.
	 * @return the key for <code>arg</code>.
	 */
	Object getKey(T arg);
	
}
