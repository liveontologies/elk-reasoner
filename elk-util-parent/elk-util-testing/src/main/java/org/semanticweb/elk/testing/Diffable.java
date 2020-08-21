package org.semanticweb.elk.testing;

/*-
 * #%L
 * ELK Utilities for Testing
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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
 * Represents an output that can be compared with other outputs
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <O>
 *            the type of the output which elements can be compared
 * @param <L>
 *            the listener using which one can report missing elements
 * 
 */
public interface Diffable<O, L> {

	boolean containsAllElementsOf(O other);

	void reportMissingElementsOf(O other, L listener);

}
