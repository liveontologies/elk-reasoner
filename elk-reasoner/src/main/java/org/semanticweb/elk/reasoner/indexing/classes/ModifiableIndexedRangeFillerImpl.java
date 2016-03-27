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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedRangeFiller;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;

/**
 * Implements {@link ModifiableIndexedRangeFiller}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableIndexedRangeFillerImpl extends IndexedObjectImpl
		implements ModifiableIndexedRangeFiller {

	private final ModifiableIndexedObjectProperty property_;

	private final ModifiableIndexedClassExpression filler_;

	private volatile ExtendedContext context_ = null;

	ModifiableIndexedRangeFillerImpl(ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression fillerConcept) {
		this.property_ = property;
		this.filler_ = fillerConcept;
	}

	@Override
	public ModifiableIndexedObjectProperty getProperty() {
		return this.property_;
	}

	@Override
	public ModifiableIndexedClassExpression getFiller() {
		return this.filler_;
	}

	@Override
	public final ExtendedContext getContext() {
		return this.context_;
	}

	@Override
	public final synchronized ExtendedContext setContextIfAbsent(
			ExtendedContext context) {
		if (context_ != null)
			return context_;
		// else
		context_ = context;
		return null;
	}

	@Override
	public final synchronized void resetContext() {
		context_ = null;
	}

	@Override
	public final <O> O accept(IndexedContextRoot.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(IndexedObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}