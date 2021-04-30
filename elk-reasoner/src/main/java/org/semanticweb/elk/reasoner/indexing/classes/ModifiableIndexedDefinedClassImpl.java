package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDefinedClass;

/**
 * Implements {@link ModifiableIndexedDefinedClass}
 * 
 * @author Yevgeny Kazakov
 * 
 */
final class ModifiableIndexedDefinedClassImpl
		extends StructuralIndexedClassEntryImpl
		implements ModifiableIndexedDefinedClass {


	/**
	 * The equivalent {@link ModifiableIndexedClassExpression} if there exists
	 * one or {@code null} otherwise
	 */
	private ModifiableIndexedClassExpression definition_;

	/**
	 * The {@link ElkAxiom} from which {@link #definition_} originates
	 */
	private ElkAxiom definitionReason_;

	
	ModifiableIndexedDefinedClassImpl(ElkClass owlThing) {
		super(owlThing);
	}

	@Override
	public IndexedClassExpression getDefinition() {
		return this.definition_;
	}

	@Override
	public ElkAxiom getDefinitionReason() {
		return this.definitionReason_;
	}

	@Override
	public boolean setDefinition(ModifiableIndexedClassExpression definition,
			ElkAxiom reason) {
		if (definition_ != null)
			return false;
		// else
		this.definition_ = definition;
		this.definitionReason_ = reason;
		return true;
	}

	@Override
	public void removeDefinition() {
		this.definition_ = null;
	}

	@Override
	public <O> O accept(IndexedClassEntity.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
