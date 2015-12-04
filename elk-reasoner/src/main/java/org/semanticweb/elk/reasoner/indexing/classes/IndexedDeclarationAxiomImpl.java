package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;

/**
 * Implements {@link IndexedDeclarationAxiom}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 * @param <E>
 *            the type of the {@link IndexedEntity} used in the axiom
 */
class IndexedDeclarationAxiomImpl<A extends ElkAxiom, E extends IndexedEntity>
		extends
			IndexedAxiomImpl<A>
		implements IndexedDeclarationAxiom {

	private final E entity_;

	IndexedDeclarationAxiomImpl(A originalAxiom, E entity) {
		super(originalAxiom);
		this.entity_ = entity;
	}

	@Override
	public final E getEntity() {
		return this.entity_;
	}

	@Override
	public final String toStringStructural() {
		return "Declaration(" + entity_.getElkEntity().getEntityType() + "("
				+ this.entity_ + "))";
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
