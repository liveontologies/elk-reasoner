/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing;

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
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of elements to which the index rule can be applied
 */
public interface IndexRule<T> {

	/**
	 * Applying the rule to an indexed element
	 * 
	 * @param element
	 *            the element to which the rule is applied
	 * @return {@code true} if the input has changed in the result of the
	 *         operation
	 */
	public boolean apply(T element);

	public boolean deapply(T element);

}
