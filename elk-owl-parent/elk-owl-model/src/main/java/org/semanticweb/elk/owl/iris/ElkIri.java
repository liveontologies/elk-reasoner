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
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * This class represents an IRI. It redefines hash and equality so that two
 * IRIs are considered equal iff their string representations are equal.  
 * 
 * @author Frantisek Simancik
 */
public abstract class ElkIri implements ElkAnnotationSubject {
	
	
	/**
	 * Returns the full IRI as a string;
	 */
	public abstract String asString();
	
	@Override
	public final int hashCode() {
		return asString().hashCode();
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof ElkIri) {
			return this.asString().equals(((ElkIri) obj).asString());
		}
		return false;	
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		//by default does nothing
		return null;
	}
	
	
}
