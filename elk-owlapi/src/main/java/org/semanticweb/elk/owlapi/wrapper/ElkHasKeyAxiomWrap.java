/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * 
 */
package org.semanticweb.elk.owlapi.wrapper;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped {@link OWLHasKeyAxiom}
 */
public class ElkHasKeyAxiomWrap<T extends OWLHasKeyAxiom> extends
		ElkAxiomWrap<T> implements ElkHasKeyAxiom {

	public ElkHasKeyAxiomWrap(T owlAxiom) {
		super(owlAxiom);
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ElkClassExpression getClassExpression() {
		return converter.convert(this.owlObject.getClassExpression());
	}

	@Override
	public Set<ElkObjectPropertyExpression> getObjectPropertyExpressions() {
		Set<ElkObjectPropertyExpression> opes = new HashSet<ElkObjectPropertyExpression>(
				this.owlObject.getObjectPropertyExpressions().size());

		for (OWLObjectPropertyExpression ope : this.owlObject
				.getObjectPropertyExpressions()) {
			opes.add(converter.convert(ope));
		}

		return opes;
	}

	@Override
	public Set<ElkDataPropertyExpression> getDataPropertyExpressions() {
		Set<ElkDataPropertyExpression> dpes = new HashSet<ElkDataPropertyExpression>(
				this.owlObject.getDataPropertyExpressions().size());

		for (OWLDataPropertyExpression dpe : this.owlObject
				.getDataPropertyExpressions()) {
			dpes.add(converter.convert(dpe));
		}

		return dpes;
	}
}