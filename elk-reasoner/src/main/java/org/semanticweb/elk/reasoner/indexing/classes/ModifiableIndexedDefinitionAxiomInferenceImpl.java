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
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDefinitionAxiomInference;
import org.semanticweb.elk.reasoner.tracing.Inference;
import org.semanticweb.elk.reasoner.tracing.InferencePrinter;

/**
 * Implements {@link ModifiableIndexedDefinitionAxiomInference}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class ModifiableIndexedDefinitionAxiomInferenceImpl<A extends ElkAxiom>
		extends
			ModifiableIndexedDefinitionAxiomImpl<A>
		implements
			ModifiableIndexedDefinitionAxiomInference {

	protected ModifiableIndexedDefinitionAxiomInferenceImpl(A originalAxiom,
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		super(originalAxiom, definedClass, definition);
	}

	@Override
	public IndexedDefinitionAxiom getConclusion(
			IndexedDefinitionAxiom.Factory factory) {
		return factory.getIndexedDefinitionAxiom(getOriginalAxiom(),
				getDefinedClass(), getDefinition());
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}
	
	@Override
	public String toString() {
		return InferencePrinter.toString(this);		
	}
	
	@Override
	public final <O> O accept(Inference.Visitor<O> visitor) {
		return accept((IndexedDefinitionAxiomInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(IndexedAxiomInference.Visitor<O> visitor) {
		return accept((IndexedDefinitionAxiomInference.Visitor<O>) visitor);
	}

}
