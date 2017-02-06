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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.Conclusion.Factory;

/**
 * A {@link ClassInference} producing a {@link Propagation} from a
 * {@link SubContextInitialization}, {@link SubClassInclusionComposed} with
 * {@link SubClassInclusionComposed#getSubsumer()} of the type
 * {@link IndexedObjectSomeValuesFrom}, and a {@link SubPropertyChain}:<br>
 * 
 * <pre>
 *    (1)      (2)     (3)
 *  ![C:R]  [C] ⊑ +D  R ⊑ S
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *       ∃[R].[C] ⊑ ∃S.D
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()} <br>
 * R = {@link #getSubDestination()}<br>
 * ∃S.D = {@link #getConclusionCarry()} (from which S and D can be obtained)<br>
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationGenerated extends AbstractPropagationInference {

	public PropagationGenerated(IndexedContextRoot inferenceRoot,
			IndexedObjectProperty subRelation,
			IndexedObjectSomeValuesFrom carry) {
		super(inferenceRoot, subRelation, carry);
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public SubContextInitialization getFirstPremise(
			SubContextInitialization.Factory factory) {
		return factory.getSubContextInitialization(getDestination(),
				getSubDestination());
	}

	public SubClassInclusionComposed getSecondPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				getCarry().getFiller());
	}

	public SubPropertyChain getThirdPremise(SubPropertyChain.Factory factory) {
		return factory.getSubPropertyChain(getSubDestination(),
				getCarry().getProperty());
	}

	@Override
	public int getPremiseCount() {
		return 3;
	}

	@Override
	public Conclusion getPremise(int index, Factory factory) {
		switch (index) {
		case 0:
			return getFirstPremise(factory);
		case 1:
			return getSecondPremise(factory);
		case 2:
			return getThirdPremise(factory);
		default:
			return failGetPremise(index);
		}
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
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(PropagationGenerated inference);

	}

}
