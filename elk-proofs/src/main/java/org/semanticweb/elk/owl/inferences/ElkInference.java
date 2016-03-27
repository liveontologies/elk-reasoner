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

public interface ElkInference {

	<O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends ElkClassInclusionExistentialFillerUnfolding.Factory,
			ElkClassInclusionExistentialOfObjectHasSelf.Factory,
			ElkClassInclusionExistentialPropertyUnfolding.Factory,
			ElkClassInclusionHierarchy.Factory,
			ElkClassInclusionObjectIntersectionOfComposition.Factory,
			ElkClassInclusionObjectIntersectionOfDecomposition.Factory,
			ElkClassInclusionObjectUnionOfComposition.Factory,
			ElkClassInclusionOfEquivalence.Factory,
			ElkClassInclusionOfObjectPropertyDomain.Factory,
			ElkClassInclusionOfReflexiveObjectProperty.Factory,
			ElkClassInclusionOwlThing.Factory,
			ElkClassInclusionReflexivePropertyRange.Factory,
			ElkClassInclusionTautology.Factory,
			ElkPropertyInclusionHierarchy.Factory,
			ElkPropertyInclusionOfTransitiveObjectProperty.Factory,
			ElkPropertyInclusionTautology.Factory,
			ElkPropertyRangePropertyUnfolding.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends ElkClassInclusionExistentialFillerUnfolding.Visitor<O>,
			ElkClassInclusionExistentialOfObjectHasSelf.Visitor<O>,
			ElkClassInclusionExistentialPropertyUnfolding.Visitor<O>,
			ElkClassInclusionHierarchy.Visitor<O>,
			ElkClassInclusionObjectIntersectionOfComposition.Visitor<O>,
			ElkClassInclusionObjectIntersectionOfDecomposition.Visitor<O>,
			ElkClassInclusionObjectUnionOfComposition.Visitor<O>,
			ElkClassInclusionOfEquivalence.Visitor<O>,
			ElkClassInclusionOfObjectPropertyDomain.Visitor<O>,
			ElkClassInclusionOfReflexiveObjectProperty.Visitor<O>,
			ElkClassInclusionOwlThing.Visitor<O>,
			ElkClassInclusionReflexivePropertyRange.Visitor<O>,
			ElkClassInclusionTautology.Visitor<O>,
			ElkPropertyInclusionHierarchy.Visitor<O>,
			ElkPropertyInclusionOfTransitiveObjectProperty.Visitor<O>,
			ElkPropertyInclusionTautology.Visitor<O>,
			ElkPropertyRangePropertyUnfolding.Visitor<O> {

		// combined interface

	}

}
