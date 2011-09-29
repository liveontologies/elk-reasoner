/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.views;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Implements helper functions for instances of {@link ElkObject}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */
public abstract class ElkObjectView<T extends ElkObject> implements ElkObject {

	public static int combinedHashCode(Object... objects) {
		return HashGenerator.combinedHashCode(objects);
	}

	public static int combinedHashCode(Iterable<Object> objects) {
		return HashGenerator.combinedHashCode(objects);
	}

	/**
	 * The {@link ElkObject} for which {@link ElkObjectView} is created. Must be
	 * initialized by constructors of subclasses.
	 */
	protected final T elkObject;

	protected final ElkObjectViewer subObjectViewer;

	protected final int hash_;

	/**
	 * Constructing {@link ElkObjectView} from {@link ElkObject}
	 * 
	 * @param elkObject
	 *            the elk object for which the helper object is constructed
	 */
	public ElkObjectView(T elkObject, ElkObjectViewer subObjectViewer) {
		this.elkObject = elkObject;
		this.subObjectViewer = subObjectViewer;
		this.hash_ = generateHashCode();
	}

	/**
	 * The function to generate hash code to be cached when the object is
	 * created.
	 * 
	 * @return
	 */
	public abstract int generateHashCode();

	public int hashCode() {
		return hash_;
	}

	/*
	 * Make sure that equals is redefined for sub-classes
	 */
	public abstract boolean equals(Object object);

	/*
	 * TODO: get rid of structural equality and hashes from the ElkObject
	 * interface
	 */
	public boolean structuralEquals(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	public int structuralHashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
