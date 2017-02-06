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
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiomInference;

/**
 * Implements {@link ModifiableIndexedEquivalentClassesAxiomInference}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class AbstractModifiableIndexedEquivalentClassesAxiomInference<A extends ElkAxiom>
		extends AbstractIndexedAxiomInference<A>
		implements ModifiableIndexedEquivalentClassesAxiomInference {

	private final ModifiableIndexedClassExpression firstMember_, secondMember_;

	protected AbstractModifiableIndexedEquivalentClassesAxiomInference(
			A originalAxiom, ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		super(originalAxiom);
		this.firstMember_ = firstMember;
		this.secondMember_ = secondMember;
	}

	public ModifiableIndexedClassExpression getFirstMember() {
		return this.firstMember_;
	}

	public ModifiableIndexedClassExpression getSecondMember() {
		return this.secondMember_;
	}

	@Override
	public final IndexedEquivalentClassesAxiom getConclusion(
			IndexedEquivalentClassesAxiom.Factory factory) {
		return factory.getIndexedEquivalentClassesAxiom(getOriginalAxiom(),
				getFirstMember(), getSecondMember());
	}

	@Override
	public final ModifiableIndexedEquivalentClassesAxiom getConclusion(
			ModifiableIndexedEquivalentClassesAxiom.Factory factory) {
		return factory.getIndexedEquivalentClassesAxiom(getOriginalAxiom(),
				getFirstMember(), getSecondMember());
	}

	@Override
	public final <O> O accept(IndexedAxiomInference.Visitor<O> visitor) {
		return accept(
				(IndexedEquivalentClassesAxiomInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(
			ModifiableIndexedAxiomInference.Visitor<O> visitor) {
		return accept(
				(ModifiableIndexedEquivalentClassesAxiomInference.Visitor<O>) visitor);
	}

}
