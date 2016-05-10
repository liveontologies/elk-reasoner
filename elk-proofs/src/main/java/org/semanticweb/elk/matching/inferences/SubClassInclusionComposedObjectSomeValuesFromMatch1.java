package org.semanticweb.elk.matching.inferences;

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

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;

public class SubClassInclusionComposedObjectSomeValuesFromMatch1 extends
		AbstractInferenceMatch<SubClassInclusionComposedObjectSomeValuesFrom>
		implements BackwardLinkMatch1Watch {

	private final IndexedContextRootMatch destinationMatch_;

	private final ElkObjectSomeValuesFrom conclusionSubsumerMatch_;

	private SubClassInclusionComposedObjectSomeValuesFromMatch1(
			SubClassInclusionComposedObjectSomeValuesFrom parent,
			IndexedContextRootMatch destinationMatch,
			ElkClassExpression subsumerMatch) {
		super(parent);
		this.destinationMatch_ = destinationMatch;
		if (subsumerMatch instanceof ElkObjectSomeValuesFrom) {
			conclusionSubsumerMatch_ = (ElkObjectSomeValuesFrom) subsumerMatch;
		} else {
			throw new ElkMatchException(getParent().getConclusionSubsumer(),
					subsumerMatch);
		}
	}

	SubClassInclusionComposedObjectSomeValuesFromMatch1(
			SubClassInclusionComposedObjectSomeValuesFrom parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		this(parent, conclusionMatch.getDestinationMatch(),
				conclusionMatch.getSubsumerGeneralMatch());
	}

	public IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public ElkObjectSomeValuesFrom getConclusionSubsumerMatch() {
		return conclusionSubsumerMatch_;
	}

	public SubClassInclusionComposedMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getConclusion(factory), destinationMatch_,
				conclusionSubsumerMatch_);
	}

	public BackwardLinkMatch1 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getBackwardLinkMatch1(
				getParent().getFirstPremise(factory), getDestinationMatch());
	}
	
	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(BackwardLinkMatch1Watch.Visitor<O> visitor) {
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

		O visit(SubClassInclusionComposedObjectSomeValuesFromMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedObjectSomeValuesFromMatch1 getSubClassInclusionComposedObjectSomeValuesFromMatch1(
				SubClassInclusionComposedObjectSomeValuesFrom parent,
				SubClassInclusionComposedMatch1 conclusionMatch);

	}

}
