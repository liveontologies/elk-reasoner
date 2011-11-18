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
package org.semanticweb.elk.reasoner.saturation.markers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Marks that an axiom holds when the precondition is non-empty. 
 * 
 * @author Frantisek Simancik
 *
 */
public final class IfNonEmptyMarker implements Marker {
	
	protected final IndexedClassExpression precondition;

	public IfNonEmptyMarker(IndexedClassExpression precondition) {
		this.precondition = precondition;
	}

	public IndexedClassExpression getPrecondition() {
		return precondition;
	}
	
	@Override
	public int hashCode() {
		return precondition.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IfNonEmptyMarker)
			return precondition.equals(((IfNonEmptyMarker) obj).precondition);
		return false;
	}
}
