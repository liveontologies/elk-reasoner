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
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObjectPropertyOfAxiomInference;
import org.semanticweb.elk.reasoner.tracing.Inference;

/**
 * Implements {@link ModifiableIndexedSubObjectPropertyOfAxiomInference}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class ModifiableIndexedSubObjectPropertyOfAxiomInferenceImpl<A extends ElkAxiom>
		extends
			ModifiableIndexedSubObjectPropertyOfAxiomImpl<A>
		implements
			ModifiableIndexedSubObjectPropertyOfAxiomInference {

	ModifiableIndexedSubObjectPropertyOfAxiomInferenceImpl(A originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		super(originalAxiom, subPropertyChain, superProperty);
	}

	@Override
	public final <O> O accept(Inference.Visitor<O> visitor) {
		return accept(
				(IndexedSubObjectPropertyOfAxiomInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedAxiomInference.Visitor<O> visitor) {
		return accept(
				(IndexedSubObjectPropertyOfAxiomInference.Visitor<O>) visitor);
	}

}
