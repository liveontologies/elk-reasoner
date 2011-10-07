/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;

/**
 * Implements the {@link ElkInverseFunctionalObjectPropertyAxiom} interface by
 * wrapping instances of {@link OWLInverseFunctionalObjectPropertyAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkInverseFunctionalObjectPropertyAxiomWrap<T extends OWLInverseFunctionalObjectPropertyAxiom>
		extends ElkObjectPropertyAxiomWrap<T> implements
		ElkInverseFunctionalObjectPropertyAxiom {

	public ElkInverseFunctionalObjectPropertyAxiomWrap(
			T owlInverseFunctionalObjectPropertyAxiom) {
		super(owlInverseFunctionalObjectPropertyAxiom);
	}

	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return converter.convert(this.owlObject.getProperty());
	}

	@Override
	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
