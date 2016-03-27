package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

public class SubClassInclusionDecomposedMatch2
		extends SubClassInclusionMatch<SubClassInclusionDecomposedMatch1> {

	SubClassInclusionDecomposedMatch2(SubClassInclusionDecomposedMatch1 parent,
			ElkClassExpression subsumerGeneralMatch) {
		super(parent, subsumerGeneralMatch);
	}

	SubClassInclusionDecomposedMatch2(SubClassInclusionDecomposedMatch1 parent,
			ElkObjectIntersectionOf subsumerFullConjunctionMatch,
			int subsumerConjunctionPrefixLength) {
		super(parent, subsumerFullConjunctionMatch,
				subsumerConjunctionPrefixLength);
	}

	@Override
	IndexedClassExpression getSubsumer() {
		return getParent().getParent().getSubsumer();
	}

	@Override
	public <O> O accept(ClassConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
				SubClassInclusionDecomposedMatch1 parent,
				ElkClassExpression subsumerMatch);

		SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
				SubClassInclusionDecomposedMatch1 parent,
				ElkObjectIntersectionOf subsumerFullConjunctionMatch,
				int subsumerConjunctionPrefixLength);

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

		O visit(SubClassInclusionDecomposedMatch2 conclusionMatch);

	}

}
