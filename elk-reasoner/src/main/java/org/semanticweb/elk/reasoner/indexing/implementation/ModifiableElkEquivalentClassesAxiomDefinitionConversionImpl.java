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

import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.inferences.IndexedDefinitionAxiomInferenceVisitor;
import org.semanticweb.elk.reasoner.indexing.inferences.ModifiableElkEquivalentClassesAxiomDefinitionConversion;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;

/**
 * Implements {@link ModifiableElkEquivalentClassesAxiomDefinitionConversion}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableElkEquivalentClassesAxiomDefinitionConversionImpl
		extends
			ModifiableIndexedDefinitionAxiomInferenceImpl<ElkEquivalentClassesAxiom>
		implements
			ModifiableElkEquivalentClassesAxiomDefinitionConversion {

	private final int definedClassPosition_, definitionPosition_;

	ModifiableElkEquivalentClassesAxiomDefinitionConversionImpl(
			ElkEquivalentClassesAxiom originalAxiom, int definedClassPosition,
			int definitionPosition, ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		super(originalAxiom, definedClass, definition);
		this.definedClassPosition_ = definedClassPosition;
		this.definitionPosition_ = definitionPosition;
	}

	@Override
	public int getDefinedClassPosition() {
		return definedClassPosition_;
	}

	@Override
	public int getDefinitionPosition() {
		return definitionPosition_;
	}

	@Override
	public <I, O> O accept(IndexedDefinitionAxiomInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
