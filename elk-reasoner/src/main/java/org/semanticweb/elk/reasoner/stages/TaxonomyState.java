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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;

/**
 * Stores information about the state of the taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyState {

	UpdateableTaxonomy<ElkClass> taxonomy = null;

	final Set<ElkClass> classesForModifiedNodes = Collections
			.newSetFromMap(new ConcurrentHashMap<ElkClass, Boolean>());

	public UpdateableTaxonomy<ElkClass> getTaxonomy() {
		return taxonomy;
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

		public void markClassForModifiedNode(final ElkClass elkClass) {
			classesForModifiedNodes.add(elkClass);
		}

		public void markClassesForModifiedNode(final Node<ElkClass> node) {
			for (ElkClass clazz : node.getMembers()) {
				markClassForModifiedNode(clazz);
			}
		}

		public void clearModifiedNodeObjects() {
			classesForModifiedNodes.clear();
		}
	}
}
