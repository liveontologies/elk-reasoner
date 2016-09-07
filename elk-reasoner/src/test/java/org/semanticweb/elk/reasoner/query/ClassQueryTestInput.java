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
package org.semanticweb.elk.reasoner.query;

import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * A {@link TestInput} of a class query test. The class returned by
 * {@link #getClassQuery()} should be queried with respect to the ontology
 * loaded from the test input.
 * 
 * @author Peter Skocovsky
 *
 * @param <C>
 *            the type of the class
 */
public interface ClassQueryTestInput<C> extends UrlTestInput {

	/**
	 * @return the class that should be queried.
	 */
	C getClassQuery();

}
