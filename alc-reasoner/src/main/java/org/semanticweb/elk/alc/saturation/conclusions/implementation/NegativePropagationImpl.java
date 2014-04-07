package org.semanticweb.elk.alc.saturation.conclusions.implementation;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.NegativePropagation;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.LocalDeterministicConclusionVisitor;

public class NegativePropagationImpl extends
		AbstractLocalDeterministicConclusion implements NegativePropagation {

	private final IndexedObjectProperty property_;
	
	private final IndexedClassExpression filler_;

	public NegativePropagationImpl(
			IndexedObjectSomeValuesFrom negatedExistential) {
		property_ = negatedExistential.getRelation();
		filler_ = negatedExistential.getFiller();
	}
	
	public NegativePropagationImpl(IndexedObjectProperty property, IndexedClassExpression filler) {
		property_ = property;
		filler_ = filler;
	}

	@Override
	public IndexedObjectProperty getRelation() {
		return property_;
	}

	@Override
	public IndexedClassExpression getNegatedCarry() {
		return filler_;
	}

	@Override
	public <I, O> O accept(LocalDeterministicConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return NegativePropagation.NAME + "(" + getRelation() + " "
				+ getNegatedCarry() + ")";
	}

}
