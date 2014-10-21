package org.semanticweb.elk.reasoner.indexing.hierarchy;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexedReflexiveObjectPropertyAxiom extends IndexedAxiom {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedReflexiveObjectPropertyAxiom.class);

	private final IndexedObjectProperty property_;

	IndexedReflexiveObjectPropertyAxiom(IndexedObjectProperty property) {
		this.property_ = property;
	}

	public IndexedPropertyChain getProperty() {
		return this.property_;
	}

	@Override
	public boolean occurs() {
		// not cached
		return false;
	}

	@Override
	public String toStringStructural() {
		return "ReflexiveObjectProperty(" + this.property_ + ')';
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	boolean updateOccurrenceNumbers(final ModifiableOntologyIndex index,
			final int increment) {

		if (property_.reflexiveAxiomOccurrenceNo == 0 && increment > 0) {
			// first occurrence of reflexivity property
			if (!index.addReflexiveProperty(property_))
				return false;
		}

		property_.reflexiveAxiomOccurrenceNo += increment;

		if (property_.reflexiveAxiomOccurrenceNo == 0 && increment < 0) {
			// no occurrence of reflexivity axiom
			if (!index.removeReflexiveProperty(property_)) {
				// revert the changes
				property_.reflexiveAxiomOccurrenceNo -= increment;
				return false;
			}
		}
		// success!
		return true;
	}
}
