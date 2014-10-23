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

import org.semanticweb.elk.owl.visitors.ElkIriVisitor;

/**
 * Represents an abbreaviated IRI. This class holds enough information to be
 * able to get the IRI both in the full and the abbreviated form.
 * 
 * @author Frantisek Simancik
 *
 */
public class ElkAbbreviatedIri extends ElkIri {

	protected final ElkPrefix prefix;
	protected final String localName;

	public ElkAbbreviatedIri(ElkPrefix prefix, String localName) {
		super(concatHashCode(prefix.getIri().getFullIriAsString(), localName));
		this.prefix = prefix;
		this.localName = localName;
	}

	public ElkPrefix getPrefix() {
		return prefix;
	}

	public String getLocalName() {
		return localName;
	}

	@Override
	public String getFullIriAsString() {
		return prefix.getIri().getFullIriAsString() + localName;
	}

	@Override
	public int compareTo(ElkIri arg) {
		if (arg instanceof ElkAbbreviatedIri
				&& this.prefix == ((ElkAbbreviatedIri) arg).prefix)
			return this.localName
					.compareTo(((ElkAbbreviatedIri) arg).localName);

		return super.compareTo(arg);
	}

	/**
	 * Returns (a+b).hashCode() without actually concatenating the strings.
	 */
	protected static int concatHashCode(String a, String b) {
		int hash = a.hashCode();
		for (int i = 0; i < b.length(); i++)
			hash = 31 * hash + b.charAt(i);
		return hash;
	}

	@Override
	public String toString() {
		return prefix.getName() + localName;
	}

	/**
	 * Accept an {@link ElkAbbreviatedIriVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkAbbreviatedIriVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkIriVisitor<O> visitor) {
		return accept((ElkAbbreviatedIriVisitor<O>) visitor);
	}

}
