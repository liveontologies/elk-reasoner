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

import java.util.Arrays;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Equivalent_Classes">Equivalent Class
 * Axiom<a> in the OWL 2 specification.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkEquivalentClassesAxiom extends ElkClassAxiom {
	
	protected final ElkClassExpression[] equivalentClassExpressions;
	
	private ElkEquivalentClassesAxiom(ElkClassExpression... equivalentClassExpressions) {
		this.equivalentClassExpressions = equivalentClassExpressions;
	}
	
	public static ElkEquivalentClassesAxiom create(ElkClassExpression... equivalentClassExpressions) { 
		return (ElkEquivalentClassesAxiom) factory.put(new ElkEquivalentClassesAxiom(equivalentClassExpressions));		
	}

	public ElkClassExpression[] getEquivalentClassExpressions() {
		return equivalentClassExpressions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralHhashCode()
	 */
	@Override
	public int structuralHashCode() {
		return computeCompositeHash(constructorHash_, equivalentClassExpressions);
	}
	
	private static final int constructorHash_ = "ElkEquivalentClassesAxiom".hashCode();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;
		
		if (object instanceof ElkEquivalentClassesAxiom)
			return Arrays.equals(equivalentClassExpressions,
					((ElkEquivalentClassesAxiom) object).equivalentClassExpressions);
		
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
