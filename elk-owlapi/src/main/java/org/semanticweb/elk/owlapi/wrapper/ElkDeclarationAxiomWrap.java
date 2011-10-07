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

import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

/**
 * Implements the {@link ElkDeclarationAxiom} interface by wrapping instances of
 * {@link OWLDeclarationAxiom}
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ElkDeclarationAxiomWrap<T extends OWLDeclarationAxiom> extends
		ElkAxiomWrap<T> implements ElkDeclarationAxiom {

	public ElkDeclarationAxiomWrap(T owlDeclarationAxiom) {
		super(owlDeclarationAxiom);
	}

	public ElkEntity getEntity() {
		return converter.convert(this.owlObject.getEntity());
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
