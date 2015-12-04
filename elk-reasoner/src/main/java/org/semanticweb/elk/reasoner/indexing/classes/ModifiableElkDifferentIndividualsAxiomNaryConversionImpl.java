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
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;

/**
 * Implements {@link ModifiableElkDifferentIndividualsAxiomNaryConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkDifferentIndividualsAxiomNaryConversionImpl
		extends
			ModifiableIndexedDisjointClassesAxiomInferenceImpl<ElkDifferentIndividualsAxiom>
		implements
			ModifiableElkDifferentIndividualsAxiomNaryConversion {

	ModifiableElkDifferentIndividualsAxiomNaryConversionImpl(
			ElkDifferentIndividualsAxiom originalAxiom,
			ModifiableIndexedClassExpressionList differentIndividuals) {
		super(originalAxiom, differentIndividuals);
	}

	@Override
	public final <O> O accept(
			IndexedDisjointClassesAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
