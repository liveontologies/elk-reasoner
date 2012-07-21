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
package org.semanticweb.elk.owl.iris;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationSubject;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * This class represents an IRI. It redefines hash and equality so that two IRIs
 * are considered equal iff their string representations are equal.
 * 
 * @author Frantisek Simancik
 * 
 */
public abstract class ElkIri implements Comparable<ElkIri>,
		ElkAnnotationSubject, ElkAnnotationValue {

	protected final int hashCode;

	/**
	 * @return the full IRI as a string
	 */
	public abstract String getFullIriAsString();

	protected ElkIri(int hashCode) {
		this.hashCode = hashCode;
	}

	@Override
	public final int hashCode() {
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj instanceof ElkIri)
			return this.hashCode == ((ElkIri) obj).hashCode
					&& this.compareTo((ElkIri) obj) == 0;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * Implements alphabetical comparison of iris. This is overriden in
	 * ElkAbbreviatedIris to optimize the case when the two iris have the same
	 * prefix.
	 */
	@Override
	public int compareTo(ElkIri arg) {
		return this.getFullIriAsString().compareTo(arg.getFullIriAsString());
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}