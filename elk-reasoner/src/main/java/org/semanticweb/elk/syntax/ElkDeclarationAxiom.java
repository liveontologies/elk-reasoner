/*
 * #%L
 * ELK Reasoner
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
/**
 * @author Yevgeny Kazakov, Jul 3, 2011
 */
package org.semanticweb.elk.syntax;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entity_Declarations_and_Typing"
 * >Declaration Axiom<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkDeclarationAxiom extends ElkAxiom {

	protected final ElkEntity entity;

	private ElkDeclarationAxiom(ElkEntity entity) {
		this.entity = entity;
		this.structuralHashCode = entity.hashCode();
	}

	public static ElkDeclarationAxiom create(ElkEntity entity) {
		return (ElkDeclarationAxiom) factory
				.put(new ElkDeclarationAxiom(entity));
	}

	public ElkEntity getEntity() {
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.syntax.ElkAxiom#accept(org.semanticweb.elk.syntax
	 * .ElkAxiomVisitor)
	 */
	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.syntax.ElkObject#structuralEquals(org.semanticweb
	 * .elk.syntax.ElkObject)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;

		if (object instanceof ElkDeclarationAxiom)
			return entity.equals(((ElkDeclarationAxiom) object).entity);

		return false;
	}

}
