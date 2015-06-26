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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Represents a subsumption inference.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SubClassOfSubsumer<S extends IndexedClassExpression> extends
		DecomposedSubsumerImpl<S> implements ClassInference {

	private final IndexedClassExpression premise_;

	private final ElkAxiom reason_;

	public SubClassOfSubsumer(IndexedContextRoot root,
			IndexedClassExpression premise, S conclusion, ElkAxiom reason) {
		super(root, conclusion);
		this.premise_ = premise;
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getInferenceContextRoot() {
		return getRoot();
	}

	public Subsumer<?> getPremise() {
		return new DecomposedSubsumerImpl<IndexedClassExpression>(
				getInferenceContextRoot(), premise_);
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	@Override
	public String toString() {
		return super.toString() + " (subclassof)";
	}

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
