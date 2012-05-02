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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;

/**
 * Implements the {@link ElkEquivalentDataPropertiesAxiom} interface by wrapping
 * instances of {@link OWLEquivalentDataPropertiesAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkEquivalentDataPropertiesAxiomWrap<T extends OWLEquivalentDataPropertiesAxiom>
		extends ElkDataPropertyAxiomWrap<T> implements
		ElkEquivalentDataPropertiesAxiom {

	public ElkEquivalentDataPropertiesAxiomWrap(
			T owlEquivalentDataPropertiesAxiom) {
		super(owlEquivalentDataPropertiesAxiom);
	}

	@Override
	public List<? extends ElkDataPropertyExpression> getDataPropertyExpressions() {
		List<ElkDataPropertyExpression> result = new ArrayList<ElkDataPropertyExpression>();
		for (OWLDataPropertyExpression dpe : this.owlObject.getProperties()) {
			result.add(converter.convert(dpe));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkDataPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}
}