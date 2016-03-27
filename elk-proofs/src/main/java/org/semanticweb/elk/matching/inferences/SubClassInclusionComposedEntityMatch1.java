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
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedEntity;

public class SubClassInclusionComposedEntityMatch1
		extends AbstractInferenceMatch<SubClassInclusionComposedEntity> {

	private final IndexedContextRootMatch originMatch_;

	private final ElkClass conclusionSubsumerMatch_;

	private SubClassInclusionComposedEntityMatch1(
			SubClassInclusionComposedEntity parent,
			IndexedContextRootMatch originMatch,
			ElkClassExpression subsumerMatch) {
		super(parent);
		this.originMatch_ = originMatch;
		if (subsumerMatch instanceof ElkClass) {
			conclusionSubsumerMatch_ = (ElkClass) subsumerMatch;
		} else {
			throw new ElkMatchException(parent.getConclusionSubsumer(),
					subsumerMatch);
		}
	}

	SubClassInclusionComposedEntityMatch1(
			SubClassInclusionComposedEntity parent,
			SubClassInclusionComposedMatch1 conclusionMatch) {
		this(parent, conclusionMatch.getDestinationMatch(),
				conclusionMatch.getSubsumerGeneralMatch());
	}

	public IndexedContextRootMatch getOriginMatch() {
		return originMatch_;
	}

	public ElkClass getConclusionSubsumerMatch() {
		return conclusionSubsumerMatch_;
	}

	public SubClassInclusionDecomposedMatch1 getPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getSubClassInclusionDecomposedMatch1(
				getParent().getPremise(factory), originMatch_);
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

		O visit(SubClassInclusionComposedEntityMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedEntityMatch1 getSubClassInclusionComposedEntityMatch1(
				SubClassInclusionComposedEntity parent,
				SubClassInclusionComposedMatch1 conclusionMatch);

	}

}
