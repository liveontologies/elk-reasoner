/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Object_Properties">Object Property<a> in
 * the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkObjectProperty extends ElkObjectPropertyExpression implements
		ElkEntity {

	protected final String iri;

	protected ElkObjectProperty(String iri) {
		this.iri = iri;
		this.structuralHashCode = iri.hashCode();
	}

	public static ElkObjectProperty create(String objectPropertyIri) {
		return (ElkObjectProperty) factory.put(new ElkObjectProperty(
				objectPropertyIri));
	}

	public static final ElkObjectProperty ELK_OWL_TOP_OBJECT_PROPERTY = new ElkObjectProperty(
			"owl:TobObjectProperty");

	public static final ElkObjectProperty ELK_OWL_BOTTOM_OBJECT_PROPERTY = new ElkObjectProperty(
			"owl:BottomObjectProperty");

	/**
	 * Get the IRI of this object property.
	 * 
	 * @return The IRI of this object property.
	 */
	public String getIri() {
		return iri;
	}

	@Override
	public String toString() {
		return iri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;

		if (object instanceof ElkObjectProperty)
			return iri.equals(((ElkObjectProperty) object).iri);

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ELKObjectPropertyExpression#accept(org.
	 * semanticweb.elk.reasoner.ELKObjectPropertyExpressionVisitor)
	 */
	@Override
	public <O> O accept(ElkObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.syntax.ElkEntity#accept(org.semanticweb.elk.syntax
	 * .ElkEntityVisitor)
	 */
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
