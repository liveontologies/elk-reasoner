/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class ObjectPropertyExpressionConverter implements
		ElkObjectPropertyExpressionVisitor<OWLObjectPropertyExpression> {

	private final OWLDataFactory factory_;
	
	ObjectPropertyExpressionConverter(OWLDataFactory f) {
		factory_ = f;
	}
	
	@Override
	public OWLObjectPropertyExpression visit(ElkObjectInverseOf ope) {
		return factory_.getOWLObjectInverseOf(ope.getObjectProperty().accept(this));
	}

	@Override
	public OWLObjectPropertyExpression visit(ElkObjectProperty ope) {
		return factory_.getOWLObjectProperty(IRI.create(ope.getIri().getFullIriAsString()));
	}

}
