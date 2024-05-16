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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkEquivalentDataPropertiesAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Equivalent_Data_Properties">Equivalent
 * Data Properties Axiom</a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkEquivalentDataPropertiesAxiomImpl extends
		ElkDataPropertyExpressionListObject implements
		ElkEquivalentDataPropertiesAxiom {

	ElkEquivalentDataPropertiesAxiomImpl(
			List<? extends ElkDataPropertyExpression> equivalentDataPropertyExpressions) {
		super(equivalentDataPropertyExpressions);
	}

	@Override
	public <O> O accept(ElkDataPropertyAxiomVisitor<O> visitor) {
		return accept((ElkEquivalentDataPropertiesAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return accept((ElkEquivalentDataPropertiesAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkEquivalentDataPropertiesAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkEquivalentDataPropertiesAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
