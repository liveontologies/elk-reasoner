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
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;

abstract class AbstractModifiableIndexedDeclarationAxiomInference<A extends ElkAxiom>
		extends AbstractIndexedAxiomInference<A>
		implements ModifiableIndexedDeclarationAxiomInference {

	private final ModifiableIndexedEntity entity_;

	AbstractModifiableIndexedDeclarationAxiomInference(A originalAxiom,
			ModifiableIndexedEntity entity) {
		super(originalAxiom);
		this.entity_ = entity;
	}

	public final ModifiableIndexedEntity getEntity() {
		return this.entity_;
	}

	@Override
	public final IndexedDeclarationAxiom getConclusion(
			IndexedDeclarationAxiom.Factory factory) {
		return factory.getIndexedDeclarationAxiom(getOriginalAxiom(),
				getEntity());
	}

	@Override
	public final ModifiableIndexedDeclarationAxiom getConclusion(
			ModifiableIndexedDeclarationAxiom.Factory factory) {
		return factory.getIndexedDeclarationAxiom(getOriginalAxiom(),
				getEntity());
	}

	@Override
	public final <O> O accept(IndexedAxiomInference.Visitor<O> visitor) {
		return accept((IndexedDeclarationAxiomInference.Visitor<O>) visitor);
	}

	@Override
	public <O> O accept(ModifiableIndexedAxiomInference.Visitor<O> visitor) {
		return accept(
				(ModifiableIndexedDeclarationAxiomInference.Visitor<O>) visitor);
	}

}
