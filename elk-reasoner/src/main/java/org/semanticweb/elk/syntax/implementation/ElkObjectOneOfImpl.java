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
package org.semanticweb.elk.syntax.implementation;

import java.util.List;

import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectVisitor;
import org.semanticweb.elk.syntax.interfaces.ElkIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkObject;
import org.semanticweb.elk.syntax.interfaces.ElkObjectOneOf;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Enumeration_of_Individuals">Enumeration of
 * Individuals<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkObjectOneOfImpl extends ElkObjectListObject<ElkIndividual>
		implements ElkObjectOneOf {

	private static final int constructorHash_ = "ElkObjectOneOf".hashCode();

	private ElkObjectOneOfImpl(List<? extends ElkIndividual> individuals) {
		super(individuals);
		this.structuralHashCode = ElkObjectImpl.computeCompositeHash(
				constructorHash_, individuals);
	}

	public List<? extends ElkIndividual> getIndividuals() {
		return elkObjects;
	}

	public static ElkObjectOneOfImpl create(
			List<? extends ElkIndividual> individuals) {
		return (ElkObjectOneOfImpl) factory.put(new ElkObjectOneOfImpl(
				individuals));
	}

	public static ElkObjectOneOf create(ElkIndividual firstIndividual,
			ElkIndividual... otherIndividuals) {
		return (ElkObjectOneOf) factory.put(new ElkObjectOneOfImpl(
				varArgsToList(firstIndividual, otherIndividuals)));
	}

	@Override
	public String toString() {
		return buildFssString("ObjectOneOf");
	}

	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkObjectOneOf) {
			return elkObjects
					.equals(((ElkObjectOneOf) object).getIndividuals());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
