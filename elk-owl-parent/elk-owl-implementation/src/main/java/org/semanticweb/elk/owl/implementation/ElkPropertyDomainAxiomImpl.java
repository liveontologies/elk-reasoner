/*
 * #%L
 * ELK OWL Model Implementation
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

import org.semanticweb.elk.owl.interfaces.ElkPropertyDomainAxiom;
import org.semanticweb.elk.owl.visitors.ElkPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkPropertyDomainAxiomVisitor;

/**
 * Implementation of {@link ElkPropertyDomainAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of the property of this axiom
 * @param <D>
 *            the type of the domain of this axiom
 */
public abstract class ElkPropertyDomainAxiomImpl<P, D> extends
		ElkPropertyAxiomImpl<P> implements ElkPropertyDomainAxiom<P, D> {

	private final D domain_;

	public ElkPropertyDomainAxiomImpl(P property, D domain) {
		super(property);
		this.domain_ = domain;
	}

	@Override
	public D getDomain() {
		return this.domain_;
	}

	@Override
	public <O> O accept(ElkPropertyAxiomVisitor<O> visitor) {
		return accept((ElkPropertyDomainAxiomVisitor<O>) visitor);
	}
}
