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

import java.util.HashSet;
import java.util.Set;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataPropertyVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class IndexedDataProperty {

	protected ElkDataProperty property;

	public IndexedDataProperty(ElkDataProperty property) {
		this.property = property;
	}

	public ElkIri getIri() {
		return property.getIri();
	}

	public ElkDataProperty getProperty() {
		return property;
	}

	public <O> O accept(IndexedDataPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * This counts how often this object occurred in the ontology.
	 */
	protected int occurrenceNo = 0;

	protected void updateOccurrenceNumber(int increment) {
		occurrenceNo += increment;
	}

	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toString() {
		return '<' + property.getIri().asString() + '>';
	}
	/**
	 * Hash code for this object.
	 */
	private final int hashCode_ = HashGenerator.generateNextHashCode();

	/**
	 * Get an integer hash code to be used for this object.
	 *
	 * @return Hash code.
	 */
	@Override
	public final int hashCode() {
		return hashCode_;
	}
}
