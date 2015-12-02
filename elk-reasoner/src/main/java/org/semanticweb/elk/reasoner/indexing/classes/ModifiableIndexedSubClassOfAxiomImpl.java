/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * Implements {@link ModifiableIndexedSubClassOfAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 */
class ModifiableIndexedSubClassOfAxiomImpl<A extends ElkAxiom> extends ModifiableIndexedAxiomImpl<A>
		implements ModifiableIndexedSubClassOfAxiom {

	private final ModifiableIndexedClassExpression subClass_, superClass_;

	ModifiableIndexedSubClassOfAxiomImpl(
			A originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		super(originalAxiom);
		this.subClass_ = subClass;
		this.superClass_ = superClass;
	}

	@Override
	public final ModifiableIndexedClassExpression getSubClass() {
		return this.subClass_;
	}

	@Override
	public final ModifiableIndexedClassExpression getSuperClass() {
		return this.superClass_;
	}

	@Override
	public final String toStringStructural() {
		return "SubClassOf(" + this.subClass_ + ' ' + this.superClass_ + ')';
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {
		return SuperClassFromSubClassRule.addRuleFor(this, index, getOriginalAxiom());
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		return SuperClassFromSubClassRule.removeRuleFor(this, index, getOriginalAxiom());
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
