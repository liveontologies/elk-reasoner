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
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;

public class SubClassInclusionComposedObjectUnionOfMatch1
		extends AbstractInferenceMatch<SubClassInclusionComposedObjectUnionOf> {

	private final IndexedContextRootMatch originMatch_;

	private final ElkObjectUnionOf conclusionSubsumerMatch_;

	private SubClassInclusionComposedObjectUnionOfMatch1(
			SubClassInclusionComposedObjectUnionOf parent,
			IndexedContextRootMatch originMatch,
			ElkClassExpression subsumerMatch) {
		super(parent);
		this.originMatch_ = originMatch;
		if (subsumerMatch instanceof ElkObjectUnionOf) {
			conclusionSubsumerMatch_ = (ElkObjectUnionOf) subsumerMatch;
		} else {
			throw new ElkMatchException(getParent().getSubsumer(),
					subsumerMatch);
		}
	}

	SubClassInclusionComposedObjectUnionOfMatch1(
			SubClassInclusionComposedObjectUnionOf parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		this(parent, conclusionMatch.getDestinationMatch(),
				conclusionMatch.getSubsumerGeneralMatch());
	}

	public IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	public ElkObjectUnionOf getConclusionSubsumerMatch() {
		return conclusionSubsumerMatch_;
	}

	public int getPosition() {
		return getParent().getPosition();
	}

	public SubClassInclusionComposedMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getConclusion(factory), originMatch_,
				conclusionSubsumerMatch_);
	}

	public SubClassInclusionComposedMatch1 getPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionComposedMatch1(
				getParent().getPremise(factory), originMatch_,
				conclusionSubsumerMatch_.getClassExpressions()
						.get(getPosition()));
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

		O visit(SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedObjectUnionOfMatch1 getSubClassInclusionComposedObjectUnionOfMatch1(
				SubClassInclusionComposedObjectUnionOf parent,
				SubClassInclusionComposedMatch1 conclusionMatch);

	}

}
