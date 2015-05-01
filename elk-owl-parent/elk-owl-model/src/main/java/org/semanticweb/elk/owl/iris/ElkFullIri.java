/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.iris;

import org.semanticweb.elk.owl.visitors.ElkFullIriVisitor;
import org.semanticweb.elk.owl.visitors.ElkIriVisitor;

/**
 * Represents a fully expanded IRI. This class is just a String wrapper.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ElkFullIri extends ElkIri {

	protected final String iri;

	public ElkFullIri(String iri) {
		super(iri.hashCode());
		this.iri = iri;
	}

	public ElkFullIri(ElkPrefix prefix, String localName) {
		this(prefix.getIri().getFullIriAsString() + localName);
	}

	@Override
	public String getFullIriAsString() {
		return iri;
	}

	@Override
	public String toString() {
		return "<" + getFullIriAsString() + ">";
	}

	/**
	 * Accept an {@link ElkFullIriVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkFullIriVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkIriVisitor<O> visitor) {
		return accept((ElkFullIriVisitor<O>) visitor);
	}

}