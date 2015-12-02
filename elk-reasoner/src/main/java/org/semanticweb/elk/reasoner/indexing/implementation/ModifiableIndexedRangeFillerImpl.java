package org.semanticweb.elk.reasoner.indexing.implementation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;

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

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedRangeFiller;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;

/**
 * Implements {@link ModifiableIndexedRangeFiller}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableIndexedRangeFillerImpl implements ModifiableIndexedRangeFiller {

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
	public String toStringStructural() {
		return "ObjectIntersectionOf(" + this.filler_ + ' '
				+ "ObjectSomeValuesFrom(ObjectInverseOf(" + property_ + ')'
				+ " owl:Thing)";
	}

	@Override
	public final String toString() {
		// use in debugging to identify the object uniquely (more or less)
		return toStringStructural() + "#" + hashCode();
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
	public <O> O accept(IndexedContextRoot.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}