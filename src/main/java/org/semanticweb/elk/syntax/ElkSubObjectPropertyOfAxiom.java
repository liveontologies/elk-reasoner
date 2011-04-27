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
 * "http://www.w3.org/TR/owl2-syntax/#Object_Subproperties">Object Subproperty
 * Axiom<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkSubObjectPropertyOfAxiom extends ElkObjectPropertyAxiom {
	protected final ElkObject subObject;
	protected final ElkObjectPropertyExpression superObjectPropertyExpression;
	
	private ElkSubObjectPropertyOfAxiom(
			ElkObject subObject,
			ElkObjectPropertyExpression superObjectPropertyExpression)
	{
		this.subObject = subObject;
		this.superObjectPropertyExpression = superObjectPropertyExpression;
	}

	public ElkObject getSubObject() {
		return subObject;
	}

	public ElkObjectPropertyExpression getSuperObjectPropertyExpression() {
		return superObjectPropertyExpression;
	}

	public static ElkSubObjectPropertyOfAxiom create(
			ElkObject subObject,
			ElkObjectPropertyExpression superObjectPropertyExpression)
	{ 
		return (ElkSubObjectPropertyOfAxiom) factory.put(
				new ElkSubObjectPropertyOfAxiom(subObject,
												superObjectPropertyExpression));		
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("SubObjectPropertyOf(");
		result.append(subObject.toString());
		result.append(" ");
		result.append(superObjectPropertyExpression.toString());
		result.append(")");
		return result.toString();
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralHhashCode()
	 */
	@Override
	public int structuralHashCode() {
		return computeCompositeHash(constructorHash_,
									subObject,
									superObjectPropertyExpression);
	}
	
	private static final int constructorHash_ = "ElkSubObjectPropertyOfAxiom".hashCode();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;
		
		if (object instanceof ElkSubObjectPropertyOfAxiom)
			return subObject.equals(
				((ElkSubObjectPropertyOfAxiom) object).subObject)
				&& superObjectPropertyExpression.equals(
				((ElkSubObjectPropertyOfAxiom) object).superObjectPropertyExpression);
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKObjectPropertyAxiom#accept(org.semanticweb
	 * .elk.reasoner.ELKObjectPropertyAxiomVisitor)
	 */
	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
