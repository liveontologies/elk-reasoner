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
package org.semanticweb.elk.owl.views;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkObjectIntersectionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */
public class ElkDatatypeRestrictionView<T extends ElkDatatypeRestriction>
		extends ElkUnaryNaryObjectView<T, ElkDatatype, ElkFacetRestriction>
		implements ElkDatatypeRestriction {

	/**
	 * Constructing {@link ElkDatatypeRestrictionView} from
	 * {@link ElkDatatypeRestriction} using a viewer
	 * 
	 * @param refElkDatatypeRestriction
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer used to access the functions for sub objects
	 */
	public ElkDatatypeRestrictionView(T refElkDatatypeRestriction,
			ElkObjectViewer subObjectViewer) {
		super(refElkDatatypeRestriction, subObjectViewer);
	}

	public ElkDatatype getDatatype() {
		return getUnarySubObjectView();
	}

	public List<? extends ElkFacetRestriction> getFacetRestrictions() {
		return getNarySubObjectViews();
	}

	@Override
	ElkDatatype getUnarySubObject() {
		return this.elkObject.getDatatype();
	}

	@Override
	Iterable<? extends ElkFacetRestriction> getNarySubObjects() {
		return this.elkObject.getFacetRestrictions();
	}

	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}