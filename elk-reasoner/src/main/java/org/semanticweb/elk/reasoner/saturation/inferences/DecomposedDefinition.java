package org.semanticweb.elk.reasoner.saturation.inferences;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.DecomposedSubsumerInferenceVisitor;

/**
 * A {@link DecomposedSubsumer} obtained from the (unique) definition of an
 * {@link IndexedClass} {@link Subsumer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see IndexedDefinitionAxiom
 */
public class DecomposedDefinition extends AbstractDecomposedSubsumerInference {

	private final IndexedClass defined_;

	private final ElkAxiom reason_;

	public DecomposedDefinition(IndexedContextRoot root, IndexedClass defined,
			IndexedClassExpression definition, ElkAxiom reason) {
		super(root, definition);
		this.defined_ = defined;
		this.reason_ = reason;
	}

	public IndexedClass getDefined() {
		return this.defined_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public DecomposedSubsumer getPremise() {
		return new DecomposedSubsumerImpl(getInferenceRoot(), defined_);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	@Override
	public String toString() {
		return super.toString() + " (definition-)";
	}

	@Override
	public <I, O> O accept(DecomposedSubsumerInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
