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

import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDeclarationAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entity_Declarations_and_Typing"
 * >declarations of built-in entities<a> in the OWL 2 specification that are
 * implicitly present in every OWL 2 ontology (see Table 5 in the link).
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public enum PredefinedElkDeclaration implements ElkDeclarationAxiom {

	OWL_THING_DECLARATION(PredefinedElkClass.OWL_THING),

	OWL_NOTHING_DECLARATION(PredefinedElkClass.OWL_NOTHING),

	OWL_TOP_OBJECT_PROPERTY_DECLARATION(
			PredefinedElkObjectProperty.OWL_TOP_OBJECT_PROPERTY),

	OWL_BOTTOM_OBJECT_PROPERTY_DECLARATION(
			PredefinedElkObjectProperty.OWL_BOTTOM_OBJECT_PROPERTY),

	;

	private final ElkEntity entity_;

	private PredefinedElkDeclaration(ElkEntity entity) {
		this.entity_ = entity;
	}

	@Override
	public ElkEntity getEntity() {
		return entity_;
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return accept((ElkDeclarationAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDeclarationAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDeclarationAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
