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

import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDifferentIndividualsAxiomVisitor;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

/**
 * Implements the {@link ElkDifferentIndividualsAxiom} interface by wrapping
 * instances of {@link OWLDifferentIndividualsAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkDifferentIndividualsAxiomWrap<T extends OWLDifferentIndividualsAxiom>
		extends ElkAssertionAxiomWrap<T> implements
		ElkDifferentIndividualsAxiom {

	public ElkDifferentIndividualsAxiomWrap(T owlDifferentIndividualsAxiom) {
		super(owlDifferentIndividualsAxiom);
	}

	@Override
	public List<? extends ElkIndividual> getIndividuals() {
		List<ElkIndividual> result = new ArrayList<ElkIndividual>();
		for (OWLIndividual ind : this.owlObject.getIndividuals()) {
			result.add(converter.convert(ind));
		}
		return result;
	}

	@Override
	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return accept((ElkDifferentIndividualsAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDifferentIndividualsAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
