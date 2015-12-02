/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link SubClassInclusionComposed} with {@link IndexedClass} super-class
 * obtained from a {@link SubClassInclusionComposed} in which the super-class is
 * its definition.
 * 
 * @see IndexedDefinitionAxiom
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class SubClassInclusionComposedDefinedClass
		extends
			AbstractSubClassInclusionComposedInference<IndexedClass> {

	private final IndexedClassExpression definition_;

	private final ElkAxiom reason_;

	/**
	 */
	public SubClassInclusionComposedDefinedClass(IndexedContextRoot inferenceRoot,
			IndexedClass defined, IndexedClassExpression definition,
			ElkAxiom reason) {
		super(inferenceRoot, defined);
		this.definition_ = definition;
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public IndexedClassExpression getDefinition() {
		return this.definition_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public SubClassInclusionComposed getPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getComposedSubClassInclusion(getInferenceRoot(),
				definition_);
	}

	public IndexedDefinitionAxiom getSideCondition(
			IndexedDefinitionAxiom.Factory factory) {
		return factory.getIndexedDefinitionAxiom(reason_, getSuperExpression(),
				definition_);
	}

	@Override
	public String toString() {
		return super.toString() + " (definition+)";
	}

	@Override
	public <O> O accept(
			SubClassInclusionComposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(SubClassInclusionComposedDefinedClass inference);
		
	}
	
}
