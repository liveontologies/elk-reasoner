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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;

/**
 * Implements the {@link ElkDatatypeRestriction} interface by wrapping instances
 * of {@link OWLDatatypeRestriction}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDatatypeRestrictionWrap<T extends OWLDatatypeRestriction>
		extends ElkDataRangeWrap<T> implements ElkDatatypeRestriction {

	public ElkDatatypeRestrictionWrap(T owlDatatypeRestriction) {
		super(owlDatatypeRestriction);
	}

	public ElkDatatype getDatatype() {
		return converter.convert(this.owlObject.getDatatype());
	}

	public List<? extends ElkFacetRestriction> getFacetRestrictions() {
		List<ElkFacetRestriction> result = new ArrayList<ElkFacetRestriction>();
		for (OWLFacetRestriction frstr : this.owlObject.getFacetRestrictions()) {
			result.add(converter.convert(frstr));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return visitor.visit(this);
	}
}