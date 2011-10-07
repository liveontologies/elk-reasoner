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

import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entity_Declarations_and_Typing"
 * >Declarations of Built-In Entities<a> in the OWL 2 specification.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public final class PredefinedElkDeclarationAxiom implements ElkDeclarationAxiom {

	protected final ElkEntity entity;

	// do not allow construction of other instances of this class
	private PredefinedElkDeclarationAxiom(ElkEntity entity) {
		this.entity = entity;
	}

	public static final PredefinedElkDeclarationAxiom OWL_THING_DECLARATION_AXIOM = new PredefinedElkDeclarationAxiom(
			PredefinedElkClass.OWL_THING);

	public static final PredefinedElkDeclarationAxiom OWL_NOTHING_DECLARATION_AXIOM = new PredefinedElkDeclarationAxiom(
			PredefinedElkClass.OWL_NOTHING);

	// TODO: add other declaration axioms

	public static final PredefinedElkDeclarationAxiom[] DECLARATIONS = {
			OWL_THING_DECLARATION_AXIOM, OWL_NOTHING_DECLARATION_AXIOM };

	public ElkEntity getEntity() {
		return entity;
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	// TODO: delete when removed from the interface
	public boolean structuralEquals(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	public int structuralHashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
