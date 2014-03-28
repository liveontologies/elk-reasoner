package org.semanticweb.elk.alc.indexing.hierarchy;
/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class OntologyIndex {

	private final IndexedObjectCache objectCache_ = new IndexedObjectCache();

	private final Set<IndexedClass> classes_ = new ArrayHashSet<IndexedClass>(
			1024);
	
	private final Set<IndexedObjectProperty> objectProperties_ = new ArrayHashSet<IndexedObjectProperty>(
			32);
	
	void addClass(IndexedClass indexedClass) {
		classes_.add(indexedClass);
	}

	void removeClass(ElkClass elkClass) {
		classes_.remove(elkClass);
	}
	
	void addObjectProperty(IndexedObjectProperty property) {
		objectProperties_.add(property);
	}

	void removeObjectProperty(IndexedObjectProperty property) {
		objectProperties_.remove(property);
	}

	public IndexedObjectCache getIndexedObjectCache() {
		return objectCache_;
	}

	public Set<IndexedClass> getIndexedClasses() {
		return classes_;
	}
	
	public Set<IndexedObjectProperty> getIndexedObjectProperties() {
		return objectProperties_;
	}

	/**
	 * Adds the given {@link IndexedObject} to this {@link OntologyIndex}
	 * 
	 * @param newObject
	 *            the object to be added
	 */
	public void add(IndexedObject newObject) {
		newObject.accept(objectCache_.inserter);
	}

	/**
	 * Removes the given {@link IndexedObject} from this {@link OntologyIndex}
	 * 
	 * @param oldObject
	 *            the object to be removed
	 * 
	 * @throws ElkUnexpectedIndexingException
	 *             if the given object does not occur in this
	 *             {@link OntologyIndex}
	 */
	public void remove(IndexedObject oldObject)
			throws ElkUnexpectedIndexingException {
		if (!oldObject.accept(objectCache_.deletor))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove indexed object from the cache " + oldObject);
	}

}
