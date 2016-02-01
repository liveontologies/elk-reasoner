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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * A {@link Propagation} of a {@link IndexedObjectSomeValuesFrom} carry
 * {@link SubClassInclusionComposed} with the filler super-class and a
 * {@link SubPropertyChain} between the {@link IndexedObjectProperty}
 * propagation relation and the property of the carry.
 * 
 * @see IndexedObjectSomeValuesFrom#getFiller()
 * @see IndexedObjectSomeValuesFrom#getProperty()
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationGenerated extends AbstractPropagationInference {

	public PropagationGenerated(IndexedContextRoot inferenceRoot,
			IndexedObjectProperty superRelation,
			IndexedObjectSomeValuesFrom carry) {
		super(inferenceRoot, superRelation, carry);
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public SubClassInclusionComposed getFirstPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				getCarry().getFiller());
	}

	public SubPropertyChain getSecondPremise(SubPropertyChain.Factory factory) {
		return factory.getSubPropertyChain(getRelation(),
				getCarry().getProperty());
	}

	@Override
	public final <O> O accept(PropagationInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {

		public O visit(PropagationGenerated inference);

	}

}
