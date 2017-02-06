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

import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentClassesAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiomInference;

/**
 * Implements {@link ModifiableElkEquivalentClassesAxiomEquivalenceConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkEquivalentClassesAxiomEquivalenceConversionImpl extends
		AbstractModifiableIndexedEquivalentClassesAxiomInference<ElkEquivalentClassesAxiom>
		implements ModifiableElkEquivalentClassesAxiomEquivalenceConversion {

	private final int firstMemberPosition_, secondMemberPosition_;

	ModifiableElkEquivalentClassesAxiomEquivalenceConversionImpl(
			ElkEquivalentClassesAxiom originalAxiom, int firstMemberPosition,
			int secondMemberPosition,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		super(originalAxiom, firstMember, secondMember);
		this.firstMemberPosition_ = firstMemberPosition;
		this.secondMemberPosition_ = secondMemberPosition;
	}

	@Override
	public int getFirstMemberPosition() {
		return firstMemberPosition_;
	}

	@Override
	public int getSecondMemberPosition() {
		return secondMemberPosition_;
	}

	@Override
	public final <O> O accept(
			IndexedEquivalentClassesAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(
			ModifiableIndexedEquivalentClassesAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
