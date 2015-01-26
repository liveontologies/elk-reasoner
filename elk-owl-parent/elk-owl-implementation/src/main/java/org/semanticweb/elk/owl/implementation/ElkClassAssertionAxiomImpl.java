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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkClassAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkClassAssertionAxiom}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkClassAssertionAxiomImpl extends ElkObjectImpl implements
		ElkClassAssertionAxiom {

	private final ElkIndividual individual_;
	private final ElkClassExpression classExpression_;

	ElkClassAssertionAxiomImpl(ElkClassExpression classExpression,
			ElkIndividual individual) {
		this.individual_ = individual;
		this.classExpression_ = classExpression;
	}

	@Override
	public ElkClassExpression getClassExpression() {
		return classExpression_;
	}

	@Override
	public ElkIndividual getIndividual() {
		return individual_;
	}

	@Override
	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return accept((ElkClassAssertionAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return accept((ElkClassAssertionAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkClassAssertionAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkClassAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
