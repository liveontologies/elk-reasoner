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
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiomInference;

/**
 * Implements {@link ModifiableIndexedSubClassOfAxiomInference}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class AbstractModifiableIndexedSubClassOfAxiomInference<A extends ElkAxiom>
		extends AbstractIndexedAxiomInference<A>
		implements ModifiableIndexedSubClassOfAxiomInference {

	private final ModifiableIndexedClassExpression subClass_, superClass_;

	AbstractModifiableIndexedSubClassOfAxiomInference(A originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		super(originalAxiom);
		this.subClass_ = subClass;
		this.superClass_ = superClass;

	}

	public final ModifiableIndexedClassExpression getSubClass() {
		return this.subClass_;
	}

	public final ModifiableIndexedClassExpression getSuperClass() {
		return this.superClass_;
	}

	@Override
	public final ModifiableIndexedSubClassOfAxiom getConclusion(
			ModifiableIndexedSubClassOfAxiom.Factory factory) {
		return factory.getIndexedSubClassOfAxiom(getOriginalAxiom(),
				getSubClass(), getSuperClass());
	}

	@Override
	public IndexedSubClassOfAxiom getConclusion(
			IndexedSubClassOfAxiom.Factory factory) {
		return factory.getIndexedSubClassOfAxiom(getOriginalAxiom(),
				getSubClass(), getSuperClass());
	}

	@Override
	public final <O> O accept(IndexedAxiomInference.Visitor<O> visitor) {
		return accept((IndexedSubClassOfAxiomInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(
			ModifiableIndexedAxiomInference.Visitor<O> visitor) {
		return accept(
				(ModifiableIndexedSubClassOfAxiomInference.Visitor<O>) visitor);
	}

}
