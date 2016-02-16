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
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;

/**
 * Stores information about the state of the instance taxonomy
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InstanceTaxonomyState {

	private UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy_ = null;

	private final Set<ElkNamedIndividual> individualsForModifiedNodes_ = Collections
			.newSetFromMap(new ConcurrentHashMap<ElkNamedIndividual, Boolean>());

	private final List<IndexedIndividual> removedIndividuals_ = new LinkedList<IndexedIndividual>();

	public UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return taxonomy_;
	}

	Set<ElkNamedIndividual> getIndividualsWithModifiedNodes() {
		return individualsForModifiedNodes_;
	}

	Collection<IndexedIndividual> getRemovedIndividuals() {
		return removedIndividuals_;
	}

	void initTaxonomy(
			UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy) {
		taxonomy_ = instanceTaxonomy;
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

		public void clearTaxonomy() {
			taxonomy_ = null;
		}

		public void markIndividualsForModifiedNode(
				Iterable<ElkNamedIndividual> individuals) {
			for (ElkNamedIndividual individual : individuals) {
				individualsForModifiedNodes_.add(individual);
			}
		}

		public void markRemovedIndividual(IndexedIndividual individual) {
			removedIndividuals_.add(individual);
		}

		public void clearModifiedNodeObjects() {
			individualsForModifiedNodes_.clear();
		}

		public void clearRemovedIndividuals() {
			removedIndividuals_.clear();
		}

		public void clear() {
			clearTaxonomy();
			clearModifiedNodeObjects();
			clearRemovedIndividuals();
		}
	}

}
