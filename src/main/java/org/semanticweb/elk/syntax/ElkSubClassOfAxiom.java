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
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Subclass_Axioms">Subclass Axiom<a> in the
 * OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkSubClassOfAxiom extends ElkClassAxiom {

	protected final ElkClassExpression subClassExpression, superClassExpression;
	
	private ElkSubClassOfAxiom(	ElkClassExpression subClassExpression,
								ElkClassExpression superClassExpression)
	{
		this.subClassExpression = subClassExpression;
		this.superClassExpression = superClassExpression;
	}

	public ElkClassExpression getSubClassExpression() {
		return subClassExpression;
	}

	public ElkClassExpression getSuperClassExpression() {
		return superClassExpression;
	}

	public static ElkSubClassOfAxiom create(
			ElkClassExpression subClassExpression,
			ElkClassExpression superClassExpression)
	{ 
		return (ElkSubClassOfAxiom) factory.put(
				new ElkSubClassOfAxiom(subClassExpression, superClassExpression));		
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("SubClassOf(");
		result.append(subClassExpression.toString());
		result.append(" ");
		result.append(superClassExpression.toString());
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
									subClassExpression, superClassExpression);
	}
	
	private static final int constructorHash_ = "ElkSubClassOfAxiom".hashCode();
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;
		
		if (object instanceof ElkSubClassOfAxiom)
			return subClassExpression.equals(
					((ElkSubClassOfAxiom) object).subClassExpression)
				&& superClassExpression.equals(
						((ElkSubClassOfAxiom) object).superClassExpression);
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKClassAxiom#accept(org.semanticweb.elk
	 * .reasoner.ELKClassAxiomVisitor)
	 */
	@Override
	public <O> O accept(ElkClassAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
