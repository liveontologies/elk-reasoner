/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.reduction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * An intermediate state of transitive reduction for an intexted class
 * expression
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class TransitiveReductionState<J extends TransitiveReductionJob<?>> {

	/**
	 * The initiator transitive reduction job
	 */
	protected final J initiatorJob;

	/**
	 * The state of the iterator over the derived super classes in the
	 * saturation for the root. Each iterator object can be used only in one
	 * state object.
	 */
	protected final Iterator<IndexedClassExpression> superClassIterator;

	/**
	 * The list containing all equivalent classes that are found up to the
	 * current state of the iterator.
	 */
	protected final List<ElkClass> equivalent;

	/**
	 * The list consisting of equivalent classes of direct super-classes
	 * together with their saturation.
	 */
	protected final List<IndexedClass> directSuperClasses;

	TransitiveReductionState(J initiatorJob,
			Iterator<IndexedClassExpression> superClassIterator) {
		this.initiatorJob = initiatorJob;
		this.equivalent = new ArrayList<ElkClass>(1);
		this.directSuperClasses = new ArrayList<IndexedClass>(1);
		this.superClassIterator = superClassIterator;
	}

	J getInitiatorJob() {
		return this.initiatorJob;
	}

	Iterator<IndexedClassExpression> getSuperClassIterator() {
		return this.superClassIterator;
	}

	List<ElkClass> getEquivalent() {
		return equivalent;
	}

	List<IndexedClass> getDirectSuperClasses() {
		return directSuperClasses;
	}

}
