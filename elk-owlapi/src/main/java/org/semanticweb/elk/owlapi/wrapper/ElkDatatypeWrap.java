/*
 * #%L
 * ELK OWL API Binding
 * *
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

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.owlapi.model.OWLDatatype;

/**
 * Implements the {@link ElkDatatype} interface by wrapping instances of
 * {@link OWLDatatype}
 *
 * @author "Yevgeny Kazakov"
 *
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDatatypeWrap<T extends OWLDatatype> extends ElkEntityWrap<T>
		implements ElkDatatype {

	protected ELDatatype elDatatype;

	public ElkDatatypeWrap(T owlDatatype) {
		super(owlDatatype);
		elDatatype = ELDatatype.getByIri(owlDatatype.getIRI().toString());
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return this.owlObject.toString();
	}

	@Override
	public String getDatatypeShortname() {
		return this.owlObject.getIRI().getFragment();
	}

	@Override
	public String getDatatypeIRI() {
		return this.owlObject.getIRI().toString();
	}

	@Override
	public ELDatatype asELDatatype() {
		return elDatatype;
	}
}
