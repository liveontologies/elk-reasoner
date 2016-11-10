package org.semanticweb.elk.matching.inferences;

import java.util.List;

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
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1Watch;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch2;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

public class ClassInconsistencyOfDisjointSubsumersMatch2 extends
		AbstractInferenceMatch<ClassInconsistencyOfDisjointSubsumersMatch1>
		implements DisjointSubsumerMatch1Watch {

	private final IndexedContextRootMatch extendedOriginMatch_;

	private final List<? extends ElkClassExpression> disjointExpressionsMatch_;

	ClassInconsistencyOfDisjointSubsumersMatch2(
			ClassInconsistencyOfDisjointSubsumersMatch1 parent,
			DisjointSubsumerMatch2 firstPremiseMatch) {
		super(parent);
		this.extendedOriginMatch_ = firstPremiseMatch
				.getExtendedDestinationMatch();
		this.disjointExpressionsMatch_ = firstPremiseMatch
				.getDisjointExpressionsMatch();
		checkEquals(firstPremiseMatch, getFirstPremiseMatch(DEBUG_FACTORY));
	}

	public IndexedContextRootMatch getExtendedOriginMatch() {
		return extendedOriginMatch_;
	}

	public List<? extends ElkClassExpression> getDisjointExpressionsMatch() {
		return disjointExpressionsMatch_;
	}

	DisjointSubsumerMatch2 getFirstPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getDisjointSubsumerMatch2(
				getParent().getFirstPremiseMatch(factory),
				getExtendedOriginMatch(), getDisjointExpressionsMatch());
	}

	public DisjointSubsumerMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getDisjointSubsumerMatch1(
				getParent().getParent().getSecondPremise(factory),
				getExtendedOriginMatch());
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(DisjointSubsumerMatch1Watch.Visitor<O> visitor) {
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

		O visit(ClassInconsistencyOfDisjointSubsumersMatch2 inferenceMatch2);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ClassInconsistencyOfDisjointSubsumersMatch2 getClassInconsistencyOfDisjointSubsumersMatch2(
				ClassInconsistencyOfDisjointSubsumersMatch1 parent,
				DisjointSubsumerMatch2 firstPremiseMatch);

	}

}
