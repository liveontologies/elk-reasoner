/*
 * #%L
 * ELK OWL Model Implementation
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkFacetRestrictionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkFacetRestriction}
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 *
 */
public class ElkFacetRestrictionImpl implements ElkFacetRestriction {

	private final ElkIri facetURI_;
	private final ElkLiteral literal_;

	ElkFacetRestrictionImpl(ElkIri facetURI, ElkLiteral literal) {
		this.facetURI_ = facetURI;
		this.literal_ = literal;
	}

	@Override
	public ElkIri getConstrainingFacet() {
		return facetURI_;
	}

	@Override
	public ElkLiteral getRestrictionValue() {
		return literal_;
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkFacetRestrictionVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkFacetRestrictionVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
