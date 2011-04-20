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
 * "http://www.w3.org/TR/owl2-syntax/#Classes">Class<a> in the OWL 2
 * specification.
 * 
 * @author Yevgeny Kazakov
 */
public class ElkClass extends ElkClassExpression {

	protected final String iri;

	protected ElkClass(String iri) {
		this.iri = iri;
	}
	
	public static ElkClass create(String iri) {
		return (ElkClass) factory.put(new ElkClass(iri));		
	}

	public static final ElkClass ELK_OWL_THING = 
		new ElkClass("owl:Thing");
	
	public static final ElkClass ELK_OWL_NOTHING = 
		new ElkClass("owl:Nothing");

	/**
	 * Get the IRI of this class.
	 * 
	 * @return The IRI of this class.
	 */
	public String getIri() {
		return iri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralHhashCode()
	 */
	@Override
	public int structuralHashCode() {
		return iri.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object)
			return true;
		
		if (object instanceof ElkClass)
			return iri.equals(((ElkClass) object).iri);
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKClassExpression#accept(org.semanticweb
	 * .elk.reasoner.ELKClassExpressionVisitor)
	 */
	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}
	
}
