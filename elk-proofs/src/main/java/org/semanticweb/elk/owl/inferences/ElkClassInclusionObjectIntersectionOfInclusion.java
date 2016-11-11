package org.semanticweb.elk.owl.inferences;

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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ if L2 ⊆ L1
 *  ObjectIntersectionOf(L1) ⊑ ObjectIntersectionOf(L2)
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionObjectIntersectionOfInclusion
		extends AbstractElkInference {

	private final static String NAME_ = "Super-Intersection";

	/**
	 * list L1 that contain all class expressions
	 */
	private final List<? extends ElkClassExpression> subClasses_;

	/**
	 * corresponding positions of elements in L2 within L1
	 */
	private final List<Integer> superPositions_;

	ElkClassInclusionObjectIntersectionOfInclusion(
			List<? extends ElkClassExpression> subClasses,
			List<Integer> subPositions) {
		this.subClasses_ = subClasses;
		this.superPositions_ = subPositions;
	}

	public List<? extends ElkClassExpression> getSubClasses() {
		return subClasses_;
	}

	public List<Integer> getSuperPositions() {
		return superPositions_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return 0;
	}

	@Override
	public ElkSubClassOfAxiom getPremise(int index, ElkObject.Factory factory) {
		return failGetPremise(index);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		List<ElkClassExpression> superClasses = new ArrayList<ElkClassExpression>(
				superPositions_.size());
		for (int pos : superPositions_) {
			superClasses.add(subClasses_.get(pos));
		}
		return factory.getSubClassOfAxiom(
				factory.getObjectIntersectionOf(subClasses_),
				factory.getObjectIntersectionOf(superClasses));
	}

	@Override
	public ElkInference getExample() {
		return new ElkClassInclusionObjectIntersectionOfInclusion(
				getClasses("C", subClasses_.size()), superPositions_);
	}

	@Override
	public <O> O accept(ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkClassInclusionObjectIntersectionOfInclusion getElkClassInclusionObjectIntersectionOfInclusion(
				List<? extends ElkClassExpression> subClasses,
				List<Integer> subPositions);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkClassInclusionObjectIntersectionOfInclusion inference);

	}

}
