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

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Data_Properties">built-in data
 * properties<a> in the OWL 2 specification, such as {@code owl:topDataProperty}
 * and {@code owl:bottomDataProperty} .
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public enum PredefinedElkDataProperty implements ElkDataProperty {

	OWL_TOP_DATA_PROPERTY(PredefinedElkIris.OWL_TOP_DATA_PROPERTY),

	OWL_BOTTOM_DATA_PROPERTY(PredefinedElkIris.OWL_BOTTOM_DATA_PROPERTY),

	;

	private final ElkIri iri_;

	private PredefinedElkDataProperty(ElkIri iri) {
		this.iri_ = iri;
	}

	@Override
	public ElkIri getIri() {
		return iri_;
	}

	@Override
	public ElkEntityType getEntityType() {
		return ElkEntityType.DATA_PROPERTY;
	}

	@Override
	public <O> O accept(ElkDataPropertyExpressionVisitor<O> visitor) {
		return accept((ElkDataPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDataPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return accept((ElkDataPropertyVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataPropertyVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
