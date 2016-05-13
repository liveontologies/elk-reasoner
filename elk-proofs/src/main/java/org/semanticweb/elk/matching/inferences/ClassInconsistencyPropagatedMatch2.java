package org.semanticweb.elk.matching.inferences;

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

public class ClassInconsistencyPropagatedMatch2
		extends AbstractInferenceMatch<ClassInconsistencyPropagatedMatch1> {

	private final ElkObjectProperty premiseRelationMatch_;
	
	private final IndexedContextRootMatch originMatch_;

	ClassInconsistencyPropagatedMatch2(
			ClassInconsistencyPropagatedMatch1 parent,
			BackwardLinkMatch2 firstPremiseMatch) {
		super(parent);
		this.premiseRelationMatch_ = firstPremiseMatch.getRelationMatch();
		this.originMatch_ = firstPremiseMatch.getDestinationMatch();		
	}
	
	public ElkObjectProperty getPremiseRelationMatch() {
		return premiseRelationMatch_;
	}

	public IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	public ClassInconsistencyMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getClassInconsistencyMatch1(
				getParent().getParent().getSecondPremise(factory),
				getOriginMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> {

		O visit(ClassInconsistencyPropagatedMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ClassInconsistencyPropagatedMatch2 getClassInconsistencyPropagatedMatch2(
				ClassInconsistencyPropagatedMatch1 parent,
				BackwardLinkMatch2 firstPremiseMatch);

	}

}
