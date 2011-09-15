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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor;
import org.semanticweb.elk.owlapi.converter.ElkClassExpressionConverterVisitor;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

/**
 * Implements the {@link ElkEquivalentClassesAxiom} interface by wrapping instances
 * of {@link OWLEquivalentClassesAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkEquivalentClassesAxiomWrap<T extends OWLEquivalentClassesAxiom>
		extends ElkClassAxiomWrap<T> implements ElkEquivalentClassesAxiom {

	public ElkEquivalentClassesAxiomWrap(T owlEquivalentClasses) {
		super(owlEquivalentClasses);
	}

	public List<? extends ElkClassExpression> getClassExpressions() {
		ElkClassExpressionConverterVisitor converter = ElkClassExpressionConverterVisitor
				.getInstance();
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>();
		for (OWLClassExpression ce : this.owlObject.getClassExpressions()) {
			result.add(ce.accept(converter));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkClassAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
