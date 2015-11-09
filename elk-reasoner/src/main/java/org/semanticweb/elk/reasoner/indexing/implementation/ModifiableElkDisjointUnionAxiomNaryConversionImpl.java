package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.reasoner.indexing.inferences.IndexedDisjointClassesAxiomInferenceVisitor;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpressionList;

/**
 * Implements {@link ModifiableElkDisjointUnionAxiomNaryConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkDisjointUnionAxiomNaryConversionImpl
		extends
			ModifiableIndexedDisjointClassesAxiomInferenceImpl<ElkDisjointUnionAxiom>
		implements
			ModifiableElkDisjointUnionAxiomNaryConversion {

	ModifiableElkDisjointUnionAxiomNaryConversionImpl(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		super(originalAxiom, disjointClasses);
	}

	@Override
	public <I, O> O accept(
			IndexedDisjointClassesAxiomInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
