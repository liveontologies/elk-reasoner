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
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link ClassInference} producing a {@link SubClassInclusionComposed} from a
 * {@link SubClassInclusionComposed} and {@link IndexedDefinitionAxiom}:<br>
 * 
 * <pre>
 *     (1)      (2)
 *  [C] ⊑ +D  [A = D]
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *      [C] ⊑ +A
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()}<br>
 * D = {@link #getDefinition()}<br>
 * A = {@link #getConclusionSubsumer()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public class SubClassInclusionComposedDefinedClass
		extends AbstractSubClassInclusionComposedInference<IndexedClass> {

	private final IndexedClassExpression definition_;

	private final ElkAxiom reason_;

	public SubClassInclusionComposedDefinedClass(
			IndexedContextRoot inferenceRoot, IndexedClass defined,
			IndexedClassExpression definition, ElkAxiom reason) {
		super(inferenceRoot, defined);
		this.definition_ = definition;
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public IndexedClassExpression getDefinition() {
		return this.definition_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public SubClassInclusionComposed getFirstPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(), definition_);
	}

	public IndexedDefinitionAxiom getSecondPremise(
			IndexedDefinitionAxiom.Factory factory) {
		return factory.getIndexedDefinitionAxiom(reason_, getSubsumer(),
				definition_);
	}

	@Override
	public final <O> O accept(
			SubClassInclusionComposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(SubClassInclusionComposedDefinedClass inference);

	}

}
