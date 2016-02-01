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

import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;

/**
 * Implements {@link ModifiableElkDeclarationAxiomConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkDeclarationAxiomConversionImpl
		extends
			ModifiableIndexedDeclarationAxiomInferenceImpl<ElkDeclarationAxiom>
		implements
			ModifiableElkDeclarationAxiomConversion {

	ModifiableElkDeclarationAxiomConversionImpl(
			ElkDeclarationAxiom originalAxiom, ModifiableIndexedEntity entity) {
		super(originalAxiom, entity);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public IndexedDeclarationAxiom getConclusion(
			IndexedDeclarationAxiom.Factory factory) {
		return factory.getIndexedDeclarationAxiom(getOriginalAxiom(),
				getEntity());
	}

	@Override
	public final <O> O accept(
			IndexedDeclarationAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}