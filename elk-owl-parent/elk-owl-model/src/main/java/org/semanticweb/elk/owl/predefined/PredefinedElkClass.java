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
package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkClassVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href= "http://www.w3.org/TR/owl2-syntax/#Classes">built-in
 * classes<a> in the OWL 2 specification, such as {@code owl:Thing} and
 * {@code owl:Nothing}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public enum PredefinedElkClass implements ElkClass {

	OWL_THING(PredefinedElkIri.OWL_THING.get()), //

	OWL_NOTHING(PredefinedElkIri.OWL_NOTHING.get())//
	;

	private final ElkIri iri_;

	private PredefinedElkClass(ElkIri iri) {
		this.iri_ = iri;
	}

	@Override
	public ElkIri getIri() {
		return iri_;
	}

	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return accept((ElkClassVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkClassVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return accept((ElkClassVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkClassVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
