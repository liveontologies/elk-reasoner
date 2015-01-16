package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Object_Properties">built-in object
 * properties<a> in the OWL 2 specification, such as
 * {@code owl:topObjectProperty} and {@code owl:bottomObjectProperty} .
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public enum PredefinedElkObjectProperty implements ElkObjectProperty {

	OWL_TOP_OBJECT_PROPERTY(PredefinedElkIris.OWL_TOP_OBJECT_PROPERTY),

	OWL_BOTTOM_OBJECT_PROPERTY(PredefinedElkIris.OWL_BOTTOM_OBJECT_PROPERTY)//
	;

	private final ElkIri iri_;

	private PredefinedElkObjectProperty(ElkIri iri) {
		this.iri_ = iri;
	}

	@Override
	public ElkIri getIri() {
		return iri_;
	}

	@Override
	public ElkEntityType getEntityType() {
		return ElkEntityType.OBJECT_PROPERTY;
	}

	@Override
	public <O> O accept(ElkObjectPropertyExpressionVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return accept((ElkObjectPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
