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

/**
 * A {@link ElkInference.Visitor} that always returns {@code null}. Can be used
 * as prototype of other visitors by overriding the default visit method.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output
 */
public class ElkInferenceDummyVisitor<O> implements ElkInference.Visitor<O> {

	protected O defaultVisit(ElkInference inference) {
		return null;
	}

	@Override
	public O visit(ElkClassInclusionExistentialFillerExpansion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionExistentialOfObjectHasSelf inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionExistentialPropertyExpansion inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionHierarchy inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionObjectIntersectionOfComposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(
			ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionObjectUnionOfComposition inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOfEquivalence inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOfObjectPropertyDomain inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOfReflexiveObjectProperty inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionOwlThing inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionReflexivePropertyRange inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkClassInclusionTautology inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyInclusionHierarchy inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyInclusionTautology inference) {
		return defaultVisit(inference);
	}

	@Override
	public O visit(ElkPropertyRangePropertyExpansion inference) {
		return defaultVisit(inference);
	}

}
