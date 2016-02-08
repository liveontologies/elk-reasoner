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

import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;

/**
 * Implements {@link ModifiableElkDisjointClassesAxiomBinaryConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkDisjointClassesAxiomBinaryConversionImpl
		extends
			ModifiableIndexedSubClassOfAxiomInferenceImpl<ElkDisjointClassesAxiom>
		implements
			ModifiableElkDisjointClassesAxiomBinaryConversion {

	private final int firstClassPosition_, secondClassPosition_;

	ModifiableElkDisjointClassesAxiomBinaryConversionImpl(
			ElkDisjointClassesAxiom originalAxiom, int firstClassPosition,
			int secondClassPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		super(originalAxiom, conjunction, bottom);
		this.firstClassPosition_ = firstClassPosition;
		this.secondClassPosition_ = secondClassPosition;
	}

	@Override
	public int getFirstClassPosition() {
		return firstClassPosition_;
	}

	@Override
	public int getSecondClassPosition() {
		return secondClassPosition_;
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
