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

import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;

/**
 * Implements {@link ModifiableElkSameIndividualAxiomConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkSameIndividualAxiomConversionImpl
		extends
			ModifiableIndexedSubClassOfAxiomInferenceImpl<ElkSameIndividualAxiom>
		implements
			ModifiableElkSameIndividualAxiomConversion {

	private final int subIndividualPosition_, superIndividualPosition_;

	ModifiableElkSameIndividualAxiomConversionImpl(
			ElkSameIndividualAxiom originalAxiom, int subIndividualPosition,
			int superIndividualPosition,
			ModifiableIndexedIndividual subIndividual,
			ModifiableIndexedIndividual superIndividual) {
		super(originalAxiom, subIndividual, superIndividual);
		this.subIndividualPosition_ = subIndividualPosition;
		this.superIndividualPosition_ = superIndividualPosition;
	}

	@Override
	public int getSubIndividualPosition() {
		return subIndividualPosition_;
	}

	@Override
	public int getSuperIndividualPosition() {
		return superIndividualPosition_;
	}

	@Override
	public IndexedSubClassOfAxiom getConclusion() {
		return this;
	}

	@Override
	public final <O> O accept(
			IndexedSubClassOfAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
