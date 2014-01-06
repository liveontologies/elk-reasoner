package org.semanticweb.elk.reasoner.indexing.hierarchy;

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

import java.io.Serializable;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;

/**
 * Top level class for all indexed objects
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class IndexedObject implements Serializable {

	/**
	 * @return {@code true} if this {@link IndexedObject} occur in the ontology
	 *         index
	 */
	public abstract boolean occurs();

	/**
	 * @return a structural string representation of the object
	 */
	abstract String toStringStructural();

	@Override
	public String toString() {
		// use in debugging to identify the object uniquely (more or less)
		return toStringStructural() + "#" + hashCode();
	}

	public abstract <O> O accept(IndexedObjectVisitor<O> visitor);

}
