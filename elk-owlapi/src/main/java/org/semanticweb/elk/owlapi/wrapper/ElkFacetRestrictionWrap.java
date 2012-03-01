/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.owlapi.model.OWLFacetRestriction;

/**
 * Implements the {@link ElkFacetRestriction} interface by wrapping instances of
 * {@link OWLFacetRestriction}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkFacetRestrictionWrap<T extends OWLFacetRestriction> extends
		ElkObjectWrap<T> implements ElkFacetRestriction {

	public ElkFacetRestrictionWrap(T owlFacetRestriction) {
		super(owlFacetRestriction);
	}

	public String getConstrainingFacet() {
		return this.owlObject.getFacet().getIRI().toString();
	}
        
	public String getConstrainingFacetShortName() {
		return this.owlObject.getFacet().getShortName();
	}

	public ElkLiteral getRestrictionValue() {
		return converter.convert(this.owlObject.getFacetValue());
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}