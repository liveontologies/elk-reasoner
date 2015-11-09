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

import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.inferences.IndexedSubObjectPropertyOfAxiomInferenceVisitor;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

/**
 * Implements {@link ModifiableElkTransitiveObjectPropertyAxiomConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkTransitiveObjectPropertyAxiomConversionImpl
		extends
			ModifiableIndexedSubObjectPropertyOfAxiomInferenceImpl<ElkTransitiveObjectPropertyAxiom>
		implements
			ModifiableElkTransitiveObjectPropertyAxiomConversion {

	ModifiableElkTransitiveObjectPropertyAxiomConversionImpl(
			ElkTransitiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		super(originalAxiom, subPropertyChain, superProperty);
	}

	@Override
	public <I, O> O accept(
			IndexedSubObjectPropertyOfAxiomInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
