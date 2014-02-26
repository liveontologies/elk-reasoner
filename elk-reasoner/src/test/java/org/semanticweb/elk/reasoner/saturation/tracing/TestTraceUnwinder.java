/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.BaseInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;

/**
 * Recursively visits all conclusions which were used to produce a given
 * conclusion. Never traces a non-traced context but stops there without
 * attempting to read traces from it.
 * 
 * Works analogously to {@link RecursiveTraceUnwinder} except that, first, it
 * can notify the caller if some visited conclusion has not been traced (this is
 * useful for testing) and, second, it will not attempt to visit inferences for
 * a conclusion which belongs to not yet traced context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestTraceUnwinder implements TraceUnwinder {

	private final TraceStore.Reader traceReader_;
	
	private final SaturationState<TracedContext> tracingState_;

	private final UntracedConclusionListener listener_;
	
	private final static InferenceVisitor<IndexedClassExpression, ?> DUMMY_INFERENCE_VISITOR = new BaseInferenceVisitor<IndexedClassExpression, Void>();

	public TestTraceUnwinder(TraceStore.Reader reader, LocalTracingSaturationState state) {
		this(reader, state, UntracedConclusionListener.DUMMY);
	}
	
	TestTraceUnwinder(TraceStore.Reader reader, SaturationState<TracedContext> state, UntracedConclusionListener listener) {
		traceReader_ = reader;
		listener_ = listener;
		tracingState_ = state;
	}
	
	public void accept(IndexedClassExpression context,
			final Conclusion conclusion,
			final ConclusionVisitor<IndexedClassExpression, ?> premiseVisitor) {
		accept(context, conclusion, premiseVisitor, DUMMY_INFERENCE_VISITOR);
	}
	
	/**
	 * 
	 * @param context
	 * @param conclusion
	 * @param premiseVisitor Visitor over all conclusions which were used as premises
	 * @param inferenceVisitor Visitor over all
	 */
	@Override
	public void accept(IndexedClassExpression context,
			final Conclusion conclusion,
			final ConclusionVisitor<IndexedClassExpression, ?> premiseVisitor,
			final InferenceVisitor<IndexedClassExpression, ?> inferenceVisitor) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Set<Inference> seenInferences = new HashSet<Inference>();

		addToQueue(context, conclusion, toDo, seenInferences, premiseVisitor, inferenceVisitor);
		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<IndexedClassExpression, ?> tracedVisitor = new PremiseVisitor<IndexedClassExpression, Void>() {

			@Override
			protected Void defaultVisit(Conclusion premise, IndexedClassExpression cxt) {
				// the context passed into this method is the context where the inference has been made
				addToQueue(cxt, premise, toDo, seenInferences, premiseVisitor, inferenceVisitor);
				return null;
			}
		};

		for (;;) {
			final InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}

			next.inference.acceptTraced(tracedVisitor, next.contextRoot);
		}
	}

	private void addToQueue(final IndexedClassExpression root, 
			final Conclusion conclusion,
			final Queue<InferenceWrapper> toDo,
			final Set<Inference> seenInferences,
			final ConclusionVisitor<IndexedClassExpression, ?> visitor,
			final InferenceVisitor<IndexedClassExpression, ?> inferenceVisitor) {
		
		Context tracedContext = tracingState_.getContext(conclusion.getSourceRoot(root));
		
		conclusion.accept(visitor, tracedContext.getRoot());
		
		if (!tracedContext.isSaturated()) {
			//must stop unwinding because the context to which the next conclusion belongs isn't yet traced
			return;
		}
		
		final MutableInteger traced = new MutableInteger(0);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(root, conclusion,
				new BaseInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(Inference inference, Void v) {
						if (!seenInferences.contains(inference)) {
							IndexedClassExpression inferenceContextRoot = inference.getInferenceContextRoot(root);
							
							inference.acceptTraced(inferenceVisitor, inferenceContextRoot);
							
							seenInferences.add(inference);
							toDo.add(new InferenceWrapper(inference, inferenceContextRoot));
						}

						traced.increment();
						
						return null;
					}

				});
		
		if (traced.get() == 0) {
			listener_.notifyUntraced(conclusion, root);
		}
	}

	/*
	 * Used to propagate context which normally isn't stored inside each traced
	 * conclusion
	 */
	private static class InferenceWrapper {

		final Inference inference;
		final IndexedClassExpression contextRoot;

		InferenceWrapper(Inference inf, IndexedClassExpression root) {
			inference = inf;
			contextRoot = root;
		}

		@Override
		public String toString() {
			return inference + " stored in " + contextRoot;
		}

	}

}
