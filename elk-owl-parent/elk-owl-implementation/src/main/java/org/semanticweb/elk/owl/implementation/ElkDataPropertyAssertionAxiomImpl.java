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

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;

/**
 * Implementation of {@link ElkDataPropertyAssertionAxiom}.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkDataPropertyAssertionAxiomImpl
		extends
		ElkPropertyAssertionAxiomImpl<ElkDataPropertyExpression, ElkIndividual, ElkLiteral>
		implements ElkDataPropertyAssertionAxiom {

	ElkDataPropertyAssertionAxiomImpl(ElkDataPropertyExpression property,
			ElkIndividual subject, ElkLiteral object) {
		super(property, subject, object);
	}

	@Override
	public <T> T accept(ElkAssertionAxiomVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
