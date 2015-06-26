/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * A {@link DecomposedSubsumer} obtained from a conjunct of an
 * {@link IndexedObjectIntersectionOf}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class DecomposedConjunction extends
		DecomposedSubsumerImpl<IndexedClassExpression> implements
		ClassInference {

	private final IndexedObjectIntersectionOf conjunction_;

	public DecomposedConjunction(IndexedContextRoot root,
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression expression) {
		super(root, expression);
		conjunction_ = conjunction;
	}

	public Subsumer<IndexedObjectIntersectionOf> getConjunction() {
		return new DecomposedSubsumerImpl<IndexedObjectIntersectionOf>(
				getInferenceContextRoot(), conjunction_);
	}

	@Override
	public IndexedContextRoot getInferenceContextRoot() {
		return getRoot();
	}

	@Override
	public String toString() {
		return super.toString() + " (conjunction-)";
	}

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
