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

import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;

/**
 * Implements {@link ModifiableElkEquivalentObjectPropertiesAxiomConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkEquivalentObjectPropertiesAxiomConversionImpl
		extends
			ModifiableIndexedSubObjectPropertyOfAxiomInferenceImpl<ElkEquivalentObjectPropertiesAxiom>
		implements
			ModifiableElkEquivalentObjectPropertiesAxiomConversion {

	private final int subPropertyPosition_, superPropertyPosition_;

	ModifiableElkEquivalentObjectPropertiesAxiomConversionImpl(
			ElkEquivalentObjectPropertiesAxiom originalAxiom,
			int subPropertyPosition, int superPropertyPosition,
			ModifiableIndexedObjectProperty subProperty,
			ModifiableIndexedObjectProperty superProperty) {
		super(originalAxiom, subProperty, superProperty);
		this.subPropertyPosition_ = subPropertyPosition;
		this.superPropertyPosition_ = superPropertyPosition;
	}

	@Override
	public int getSubPropertyPosition() {
		return subPropertyPosition_;
	}

	@Override
	public int getSuperPropertyPosition() {
		return superPropertyPosition_;
	}

	@Override
	public <O> O accept(
			IndexedSubObjectPropertyOfAxiomInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
