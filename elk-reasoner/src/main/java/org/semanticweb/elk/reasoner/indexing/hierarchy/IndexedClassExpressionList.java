/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

/**
 * Represents a list of {@link ElkClassExpression}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedClassExpressionList extends IndexedObject {

	/**
	 * @return {@link IndexedClassExpression}s occurring in this
	 *         {@link IndexedClassExpressionList}
	 */
	public List<? extends IndexedClassExpression> getElements();

	/**
	 * @return {@code true} if this {@link IndexedClassExpressionList} occurs in
	 *         the ontology
	 */
	public boolean occurs();

}
