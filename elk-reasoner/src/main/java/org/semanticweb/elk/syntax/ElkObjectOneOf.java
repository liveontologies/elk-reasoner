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
package org.semanticweb.elk.syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Enumeration_of_Individuals">Enumeration of
 * Individuals<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectOneOf extends ElkClassExpression {

	private static final int constructorHash_ = "ElkObjectOneOf".hashCode();

	protected final List<? extends ElkIndividual> individuals;

	private ElkObjectOneOf(List<? extends ElkIndividual> individuals) {
		this.individuals = individuals;
		this.structuralHashCode = ElkObject.computeCompositeHash(
				constructorHash_, individuals);
	}

	public static ElkObjectOneOf create(
			List<? extends ElkIndividual> individuals) {
		return (ElkObjectOneOf) factory.put(new ElkObjectOneOf(individuals));
	}

	public static ElkObjectOneOf create(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals) {
		List<ElkIndividual> individuals = new ArrayList<ElkIndividual>(
				1 + otherIndividuals.length);
		individuals.add(firstIndividual);
		for (int i = 0; i < otherIndividuals.length; ++i)
			individuals.add(otherIndividuals[i]);
		return (ElkObjectOneOf) factory.put(new ElkObjectOneOf(individuals));
	}

	public List<? extends ElkIndividual> getIndividuals() {
		return individuals;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ObjectOneOf(");
		for (ElkIndividual ce : individuals) {
			result.append(ce.toString());
			result.append(" ");
		}
		result.setCharAt(result.length() - 1, ')');
		return result.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectOneOf) {
			return individuals.equals(((ElkObjectOneOf) object).individuals);
		} else {
			return false;
		}
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
