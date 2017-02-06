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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.Conclusion.Factory;

// TODO: split on two inferences
/**
 * A {@link ClassInference} producing a {@link SubClassInclusionDecomposed} from
 * {@link ContextInitialization}:<br>
 * 
 * <pre>
 *   ![C]
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  [C] ⊑ -C
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()} =
 * {@link #getConclusionSubsumer()}<br>
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public class SubClassInclusionTautology
		extends AbstractSubClassInclusionDecomposedInference {

	private static IndexedContextRoot.Visitor<IndexedClassExpression> SUBSUMER_EXTRACTOR_ = new SubsumerExtractor();

	public SubClassInclusionTautology(IndexedContextRoot inferenceRoot) {
		super(inferenceRoot, inferenceRoot.accept(SUBSUMER_EXTRACTOR_));
	}

	private static class SubsumerExtractor
			extends DummyIndexedContextRootVisitor<IndexedClassExpression> {

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

	public ContextInitialization getPremise(
			ContextInitialization.Factory factory) {
		return factory.getContextInitialization(getOrigin());
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public Conclusion getPremise(int index, Factory factory) {
		switch (index) {
		case 0:
			return getPremise(factory);
		default:
			return failGetPremise(index);
		}
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
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(SubClassInclusionTautology inference);

	}

}
