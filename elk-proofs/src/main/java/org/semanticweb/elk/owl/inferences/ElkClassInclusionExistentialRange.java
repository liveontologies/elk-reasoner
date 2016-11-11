package org.semanticweb.elk.owl.inferences;

import java.util.ArrayList;
import java.util.Arrays;

/*-
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *           (1)                             (n)
 *  ObjectPropertyRange(R, E1) ... ObjectPropertyRange(R, En)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *                   ∃R.D ⊑ ∃R.(D ⊓ E1 ⊓...⊓ En)
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionExistentialRange extends AbstractElkInference {

	private final static String NAME_ = "Existential Range Intersection";

	private final ElkObjectPropertyExpression property_;

	private final ElkClassExpression filler_;

	private final List<? extends ElkClassExpression> ranges_;

	ElkClassInclusionExistentialRange(ElkObjectPropertyExpression property,
			ElkClassExpression filler,
			List<? extends ElkClassExpression> ranges) {
		if (ranges.size() < 1) {
			throw new IllegalArgumentException("No property ranges");
		}
		this.property_ = property;
		this.filler_ = filler;
		this.ranges_ = ranges;
	}

	ElkClassInclusionExistentialRange(ElkObjectPropertyExpression property,
			ElkClassExpression filler, ElkClassExpression... ranges) {
		this(property, filler, Arrays.asList(ranges));
	}

	public ElkObjectPropertyExpression getProperty() {
		return property_;
	}

	public ElkClassExpression getFiller() {
		return filler_;
	}

	public List<? extends ElkClassExpression> getRanges() {
		return ranges_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return ranges_.size();
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObject.Factory factory) {
		checkPremiseIndex(index);
		return getRangePremise(index, factory);
	}

	public ElkObjectPropertyRangeAxiom getRangePremise(int index,
			ElkObject.Factory factory) {
		return factory.getObjectPropertyRangeAxiom(property_,
				ranges_.get(index));
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		List<ElkClassExpression> newFillers = new ArrayList<ElkClassExpression>(
				ranges_.size() + 1);
		newFillers.add(filler_);
		newFillers.addAll(ranges_);
		return factory.getSubClassOfAxiom(
				factory.getObjectSomeValuesFrom(property_, filler_),
				factory.getObjectSomeValuesFrom(property_,
						factory.getObjectIntersectionOf(newFillers)));

	}

	@Override
	public ElkInference getExample() {
		return new ElkClassInclusionExistentialRange(getObjectProperty("R"),
				getClass("D"), getClasses("E", ranges_.size()));
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

		ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
				ElkObjectPropertyExpression property, ElkClassExpression filler,
				List<? extends ElkClassExpression> ranges);

		ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
				ElkObjectPropertyExpression property, ElkClassExpression filler,
				ElkClassExpression... ranges);

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

		O visit(ElkClassInclusionExistentialRange inference);

	}

}
