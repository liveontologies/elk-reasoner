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
import org.semanticweb.elk.reasoner.Inference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDisjointClassesAxiomInference;

/**
 * Implements {@link ModifiableIndexedDisjointClassesAxiomInference}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class ModifiableIndexedDisjointClassesAxiomInferenceImpl<A extends ElkAxiom>
		extends
			ModifiableIndexedDisjointClassesAxiomImpl<A>
		implements
			ModifiableIndexedDisjointClassesAxiomInference {

	ModifiableIndexedDisjointClassesAxiomInferenceImpl(A originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		super(originalAxiom, members);
	}

	@Override
	public <O> O accept(Inference.Visitor<O> visitor) {
		return accept(
				(IndexedDisjointClassesAxiomInference.Visitor<O>) visitor);
	}
	
	@Override
	public <O> O accept(IndexedAxiomInference.Visitor<O> visitor) {
		return accept(
				(IndexedDisjointClassesAxiomInference.Visitor<O>) visitor);
	}
	
}
