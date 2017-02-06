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

import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiomInference;

/**
 * Implements {@link ModifiableElkDifferentIndividualsAxiomBinaryConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkDifferentIndividualsAxiomBinaryConversionImpl extends
		AbstractModifiableIndexedSubClassOfAxiomInference<ElkDifferentIndividualsAxiom>
		implements ModifiableElkDifferentIndividualsAxiomBinaryConversion {

	private final int firstIndividualPosition_, secondIndividualPosition_;

	ModifiableElkDifferentIndividualsAxiomBinaryConversionImpl(
			ElkDifferentIndividualsAxiom originalAxiom,
			int firstIndividualPosition, int secondIndividualPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClassExpression bottom) {
		super(originalAxiom, conjunction, bottom);
		this.firstIndividualPosition_ = firstIndividualPosition;
		this.secondIndividualPosition_ = secondIndividualPosition;
	}

	@Override
	public int getFirstIndividualPosition() {
		return firstIndividualPosition_;
	}

	@Override
	public int getSecondIndividualPosition() {
		return secondIndividualPosition_;
	}

	@Override
	public final <O> O accept(
			IndexedSubClassOfAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(
			ModifiableIndexedSubClassOfAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
