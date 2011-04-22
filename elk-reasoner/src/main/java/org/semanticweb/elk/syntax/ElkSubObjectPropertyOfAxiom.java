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
	protected final ElkObjectPropertyExpression subObjectPropertyExpression,
											superObjectPropertyExpression;
	
	private ElkSubObjectPropertyOfAxiom(
			ElkObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression)
	{
		this.subObjectPropertyExpression = subObjectPropertyExpression;
		this.superObjectPropertyExpression = superObjectPropertyExpression;
	}

	public ElkObjectPropertyExpression getSubObjectPropertyExpression() {
		return subObjectPropertyExpression;
	}

	public ElkObjectPropertyExpression getSuperObjectPropertyExpression() {
		return superObjectPropertyExpression;
	}

	public static ElkSubObjectPropertyOfAxiom create(
			ElkObjectPropertyExpression subObjectPropertyExpression,
			ElkObjectPropertyExpression superObjectPropertyExpression)
	{ 
		return (ElkSubObjectPropertyOfAxiom) factory.put(
				new ElkSubObjectPropertyOfAxiom(subObjectPropertyExpression,
												superObjectPropertyExpression));		
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("SubObjectPropertyOf(");
		result.append(subObjectPropertyExpression.toString());
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
									subObjectPropertyExpression,
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
			return subObjectPropertyExpression.equals(
				((ElkSubObjectPropertyOfAxiom) object).subObjectPropertyExpression)
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
