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
package org.semanticweb.elk.owl.implementation;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeRestrictionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

public class ElkDatatypeRestrictionImpl extends ElkObjectImpl implements
		ElkDatatypeRestriction {

	private final ElkDatatype datatype_;
	private final List<ElkFacetRestriction> facetRestrictions_;

	ElkDatatypeRestrictionImpl(ElkDatatype datatype,
			List<ElkFacetRestriction> restrictions) {
		this.datatype_ = datatype;
		this.facetRestrictions_ = restrictions;
	}

	@Override
	public ElkDatatype getDatatype() {
		return datatype_;
	}

	@Override
	public List<ElkFacetRestriction> getFacetRestrictions() {
		return facetRestrictions_;
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return accept((ElkDatatypeRestrictionVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDatatypeRestrictionVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDatatypeRestrictionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
