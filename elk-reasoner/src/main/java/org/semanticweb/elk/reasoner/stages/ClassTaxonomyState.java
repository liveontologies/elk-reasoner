/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;

/**
 * Stores information about the state of the class taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassTaxonomyState {

	private UpdateableTaxonomy<ElkClass> taxonomy_ = null;

	private final Set<ElkClass> classesForModifiedNodes_ = Collections
			.newSetFromMap(new ConcurrentHashMap<ElkClass, Boolean>());

	private final List<IndexedClass> modifiedClasses_ = new LinkedList<IndexedClass>();

	private final List<IndexedClass> removedClasses_ = new LinkedList<IndexedClass>();

	public UpdateableTaxonomy<ElkClass> getTaxonomy() {
		return taxonomy_;
	}

	Set<ElkClass> getClassesWithModifiedNodes() {
		return classesForModifiedNodes_;
	}

	Collection<IndexedClass> getModifiedClasses() {
		return modifiedClasses_;
	}

	Collection<IndexedClass> getRemovedClasses() {
		return removedClasses_;
	}

	public Writer getWriter() {
		return new Writer();
	}

	/**
	 * Groups all methods to change the state
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Writer {

		void setTaxonomy(UpdateableTaxonomy<ElkClass> classTaxonomy) {
			taxonomy_ = classTaxonomy;
		}

		public void clearTaxonomy() {
			taxonomy_ = null;
		}

		public void markClassForModifiedNode(final ElkClass elkClass) {
			classesForModifiedNodes_.add(elkClass);
		}

		public void markClassesForModifiedNode(final Node<ElkClass> node) {
			synchronized (node) {
				for (ElkClass clazz : node.getMembers()) {
					markClassForModifiedNode(clazz);
				}
			}
		}

		public void markModifiedClass(final IndexedClass clazz) {
			modifiedClasses_.add(clazz);
		}

		public void markRemovedClass(final IndexedClass clazz) {
			removedClasses_.add(clazz);
		}

		public void clearModifiedNodeObjects() {
			classesForModifiedNodes_.clear();
		}

		public void clearModifiedClasses() {
			modifiedClasses_.clear();
		}

		public void clearRemovedClasses() {
			removedClasses_.clear();
		}

		public void clear() {
			clearTaxonomy();
			clearModifiedNodeObjects();
			clearModifiedClasses();
			clearRemovedClasses();
		}
	}
}
