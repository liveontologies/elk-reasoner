/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.readers;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.tracing.DelegatingTraceReader;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Reads all inferences for the given conclusion except that propagations which
 * propagate the context's root over a backward link which has been created by
 * decomposing the propagated subsumer.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AvoidTrivialPropagationReader extends DelegatingTraceReader {

	public AvoidTrivialPropagationReader(TraceStore.Reader r) {
		super(r);
	}

	@Override
	public void accept(final IndexedContextRoot root,
			final Conclusion conclusion, final ClassInferenceVisitor<IndexedContextRoot, ?> visitor) {
		reader.accept(root, conclusion,
				new AbstractClassInferenceVisitor<IndexedContextRoot, Void>() {

					@Override
					protected Void defaultTracedVisit(ClassInference inference,
							IndexedContextRoot ignored) {
						inference.acceptTraced(visitor, null);

						return null;
					}

					@Override
					public Void visit(PropagatedSubsumer propagated,
							IndexedContextRoot ignored) {
						if (!isTrivialPropagation(propagated, root)) {
							propagated.acceptTraced(visitor, null);
						}

						return null;
					}

				});
	}

	boolean isTrivialPropagation(PropagatedSubsumer propagated,
			IndexedContextRoot contextRoot) {
		// a propagation is trivial if two conditions are met:
		// 1) the root is propagated (not one of its subsumers)
		// 2) the backward link has been derived by decomposing the existential
		// (which is the same as the propagation carry)
		BackwardLink link = propagated.getBackwardLink();
		Propagation propagation = propagated.getPropagation();
		IndexedContextRoot inferenceContextRoot = propagated
				.getInferenceContextRoot(contextRoot);

		if (inferenceContextRoot != IndexedObjectSomeValuesFrom.Helper
				.getTarget(propagation.getCarry())) {
			return false;
		}

		final MutableBoolean linkProducedByDecomposition = new MutableBoolean(
				false);

		reader.accept(inferenceContextRoot, link,
				new AbstractClassInferenceVisitor<IndexedContextRoot, Boolean>() {

					@Override
					protected Boolean defaultTracedVisit(ClassInference conclusion,
							IndexedContextRoot ignored) {
						return false;
					}

					@Override
					public Boolean visit(
							DecomposedExistentialBackwardLink conclusion,
							IndexedContextRoot ignored) {
						linkProducedByDecomposition.set(true);

						return true;
					}

				});

		return linkProducedByDecomposition.get();
	}

}
