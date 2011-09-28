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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkDataPropertyDomainAxiom.
 * 
 * @author Markus Kroetzsch
 */
public class ElkDataPropertyDomainAxiomImpl extends
		ElkDataPropertyExpressionDataRangeObject implements
		ElkDataPropertyDomainAxiom {

	private static final int constructorHash_ = "ElkDataPropertyDomainAxiom"
			.hashCode();

	ElkDataPropertyDomainAxiomImpl(
			ElkDataPropertyExpression dataPropertyExpression,
			ElkDataRange dataRange) {
		super(dataPropertyExpression, dataRange);
		this.structuralHashCode = HashGenerator.combineListHash(
				constructorHash_,
				dataPropertyExpression.structuralHashCode(),
				dataRange.structuralHashCode());
	}

	@Override
	public String toString() {
		return buildFssString("DataPropertyDomain");
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkDataPropertyDomainAxiom) {
			return dataPropertyExpression
					.equals(((ElkDataPropertyDomainAxiom) object)
							.getDataPropertyExpression())
					&& dataRange
							.equals(((ElkDataPropertyDomainAxiom) object)
									.getDataRange());
		} else {
			return false;
		}
	}

	public <O> O accept(ElkDataPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
