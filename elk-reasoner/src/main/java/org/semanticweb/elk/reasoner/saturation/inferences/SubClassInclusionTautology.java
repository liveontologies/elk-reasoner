/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedContextRootVisitor;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedRangeFiller;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * {@link SubClassInclusionDecomposed} representing a tautology {@code C ⊑ C}
 * obtained from no premises.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public class SubClassInclusionTautology
		extends
			AbstractSubClassInclusionDecomposedInference {

	private static IndexedContextRoot.Visitor<IndexedClassExpression> SUBSUMER_EXTRACTOR_ = new SubsumerExtractor();

	public SubClassInclusionTautology(IndexedContextRoot inferenceRoot) {
		super(inferenceRoot, inferenceRoot.accept(SUBSUMER_EXTRACTOR_));
	}

	private static class SubsumerExtractor
			extends
				DummyIndexedContextRootVisitor<IndexedClassExpression> {

		@Override
		protected IndexedClassExpression defaultVisit(
				IndexedClassExpression element) {
			return element;
		}

		@Override
		public IndexedClassExpression visit(IndexedRangeFiller element) {
			return element.getFiller();
		}
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	@Override
	public String toString() {
		return super.toString() + " (init)";
	}

	@Override
	public final <O> O accept(
			SubClassInclusionDecomposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {

		public O visit(SubClassInclusionTautology inference);

	}

}
