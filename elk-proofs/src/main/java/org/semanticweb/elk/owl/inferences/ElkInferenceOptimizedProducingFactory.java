package org.semanticweb.elk.owl.inferences;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * An {@link ElkInference.Factory} that can simplify the produced inferences,
 * e.g., by removing unnecessary premises or rewriting to simpler (sequences) of
 * inferences. The inferences are produced using the provided
 * {@link ElkInferenceProducer}. All factory methods return {@code null}.
 * 
 * @author Yevgeny Kazakov
 */

public class ElkInferenceOptimizedProducingFactory
		extends ElkInferenceProducingFactory {

	private final ElkObject.Factory elkFactory_;

	public ElkInferenceOptimizedProducingFactory(
			ElkInferenceProducer inferenceProducer) {
		this(inferenceProducer, new ElkObjectBaseFactory());
	}

	public ElkInferenceOptimizedProducingFactory(
			ElkInferenceProducer inferenceProducer,
			ElkObject.Factory elkFactory) {
		super(inferenceProducer);
		this.elkFactory_ = elkFactory;
	}

	@Override
	public ElkClassInclusionExistentialComposition getElkClassInclusionExistentialComposition(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		switch (subChain.size()) {

		case 0:
			throw new IllegalArgumentException(
					subChain.toString() + " should not be empty");
		case 1:
			ElkObjectPropertyExpression subProperty = subChain.get(0);
			ElkClassExpression subClass = classExpressions.get(0);
			ElkClassExpression filler = classExpressions.get(1);
			super.getElkClassInclusionExistentialPropertyExpansion(subProperty,
					superProperty, filler);
			super.getElkClassInclusionHierarchy(subClass,
					elkFactory_.getObjectSomeValuesFrom(subProperty, filler),
					elkFactory_.getObjectSomeValuesFrom(superProperty, filler));
			return null;
		default:
			super.getElkClassInclusionExistentialComposition(classExpressions,
					subChain, superProperty);
			return null;
		}
	}

	@Override
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			ElkObjectPropertyExpression transitiveProperty,
			ElkClassExpression... classExpressions) {
		return getElkClassInclusionExistentialTransitivity(transitiveProperty,
				Arrays.asList(classExpressions));
	}

	@Override
	public ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
			ElkObjectPropertyExpression transitiveProperty,
			List<? extends ElkClassExpression> classExpressions) {
		classExpressions = removeRepetitions(classExpressions);
		if (classExpressions.size() > 2) {// otherwise the inference is trivial
			super.getElkClassInclusionExistentialTransitivity(
					transitiveProperty, classExpressions);
		}
		return null;

	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			ElkClassExpression... expressions) {
		return getElkClassInclusionHierarchy(Arrays.asList(expressions));
	}

	@Override
	public ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
			List<? extends ElkClassExpression> expressions) {
		expressions = removeRepetitions(expressions);
		if (expressions.size() > 2) {// otherwise the inference is trivial
			super.getElkClassInclusionHierarchy(expressions);
		}
		return null;
	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			ElkObjectPropertyExpression... expressions) {
		return getElkPropertyInclusionHierarchy(subExpression,
				Arrays.asList(expressions));

	}

	@Override
	public ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
			ElkSubObjectPropertyExpression subExpression,
			List<? extends ElkObjectPropertyExpression> expressions) {
		expressions = removeRepetitions(expressions);
		if (expressions.size() > 1) {// otherwise the inference is trivial
			super.getElkPropertyInclusionHierarchy(subExpression, expressions);
		}
		return null;
	}

	@Override
	public ElkClassInclusionExistentialRange getElkClassInclusionExistentialRange(
			ElkObjectPropertyExpression property, ElkClassExpression filler,
			List<? extends ElkClassExpression> ranges) {
		if (ranges.isEmpty()) {
			return null;
		}
		// else
		super.getElkClassInclusionExistentialRange(property, filler, ranges);
		return null;
	}

	/**
	 * @param input
	 * @return The list obtained from the input list by repeatedly removing
	 *         elements after each element until its duplicate (if there is
	 *         one). The resulting list is, thus, duplicate-free. The original
	 *         list is not modified.
	 */
	<E> List<? extends E> removeRepetitions(List<? extends E> input) {
		// compute the last positions of each element in the list
		Map<E, Integer> lastPositions = new HashMap<E, Integer>(input.size());
		for (int pos = 0; pos < input.size(); pos++) {
			lastPositions.put(input.get(pos), pos);
		}
		int uniqueCount = lastPositions.size();
		if (input.size() == uniqueCount) {
			// no duplicates
			return input;
		}
		List<E> result = new ArrayList<E>(uniqueCount); // might not contain all
														// unique elements
		int nextPos = 0; // position starting from which to include elements in
							// the result
		for (int pos = 0; pos < input.size(); pos++) {
			if (pos < nextPos) {
				continue;
			}
			// else
			E e = input.get(pos);
			result.add(e);
			nextPos = lastPositions.get(e) + 1;
		}
		return result;
	}

}
