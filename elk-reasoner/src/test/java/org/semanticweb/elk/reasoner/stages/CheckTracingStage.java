/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.BaseTracedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Reader;
import org.semanticweb.elk.reasoner.saturation.tracing.TracedConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TracedPropagation;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs checks to verify that tracing information is correct
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CheckTracingStage extends BasePostProcessingStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CheckTracingStage.class);

	/**
	 * 
	 */
	public CheckTracingStage(AbstractReasonerState r) {
		super(r);
	}

	@Override
	public String getName() {
		return "Check tracing";
	}

	@Override
	public void execute() throws ElkException {
		TraceStore.Reader traceReader = reasoner.traceState.getTraceStore()
				.getReader();

		for (Context context : reasoner.saturationState.getContexts()) {
			if (reasoner.traceState.getSaturationState().isTraced(context)) {

				for (IndexedClassExpression subsumer : context.getSubsumers()) {
					checkTrace(context,
							TracingUtils.getSubsumerWrapper(subsumer),
							traceReader);
				}

				if (context.isInconsistent()) {
					checkTrace(context,
							TracingUtils
									.getSubsumerWrapper(reasoner.ontologyIndex
											.getIndexedOwlNothing()),
							traceReader);
				}
			}
		}

	}

	private void checkTrace(Context context, Conclusion conclusion,
			final Reader traceReader) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Set<TracedConclusion> seenInferences = new HashSet<TracedConclusion>();

		addToQueue(context, conclusion, toDo, traceReader, seenInferences);

		for (;;) {
			InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}

			final Context infContext = next.context;

			next.inference.acceptTraced(
					new BaseTracedConclusionVisitor<Void, Void>() {

						@Override
						public Void visit(InitializationSubsumer inference,
								Void v) {
							return null;
						}

						@Override
						public Void visit(SubClassOfSubsumer inference, Void v) {
							addToQueue(infContext, inference.getPremise(),
									toDo, traceReader, seenInferences);
							return null;
						}

						@Override
						public Void visit(ComposedConjunction inference, Void v) {
							addToQueue(infContext,
									inference.getFirstConjunct(), toDo,
									traceReader, seenInferences);
							addToQueue(infContext,
									inference.getSecondConjunct(), toDo,
									traceReader, seenInferences);
							return null;
						}

						@Override
						public Void visit(DecomposedConjunction inference,
								Void v) {
							addToQueue(infContext, inference.getConjunction(),
									toDo, traceReader, seenInferences);
							return null;
						}

						@Override
						public Void visit(ComposedBackwardLink inference, Void v) {
							// System.out.println(inference);

							addToQueue(infContext, inference.getBackwardLink(),
									toDo, traceReader, seenInferences);
							addToQueue(infContext, inference.getForwardLink(),
									toDo, traceReader, seenInferences);
							return null;
						}

						@Override
						public Void visit(ReflexiveSubsumer inference, Void v) {
							// TODO
							return null;
						}

						@Override
						public Void visit(PropagatedSubsumer inference, Void v) {
							addToQueue(infContext, inference.getBackwardLink(),
									toDo, traceReader, seenInferences);
							addToQueue(infContext, inference.getPropagation(),
									toDo, traceReader, seenInferences);
							return null;
						}

						@Override
						public Void visit(TracedPropagation inference, Void v) {
							addToQueue(infContext, inference.getPremise(),
									toDo, traceReader, seenInferences);
							return null;
						}

					}, null);
		}
	}

	private void addToQueue(final Context context, final Conclusion conclusion,
			final Queue<InferenceWrapper> toDo,
			final TraceStore.Reader traceReader,
			final Set<TracedConclusion> seenInferences) {
		// just need a mutable flag that can be set from inside the visitor
		final AtomicBoolean infFound = new AtomicBoolean(false);
		// finding all inferences that produced the input conclusion
		traceReader.accept(context, conclusion,
				new BaseTracedConclusionVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(
							TracedConclusion inference, Void v) {
						if (!seenInferences.contains(inference)) {
							seenInferences.add(inference);
							toDo.add(new InferenceWrapper(inference, inference
									.getInferenceContext(context)));
						}

						infFound.set(true);

						return null;
					}

				});

		if (!infFound.get()
				/*&& reasoner.traceState.getSaturationState().isTraced(context)*/) {
			LOGGER_.error("No inferences for a conclusion {} in context {}",
					conclusion, context);
		}
	}

	/*
	 * used to propagate context which normally isn't stored within the
	 * inference.
	 */
	private static class InferenceWrapper {

		final TracedConclusion inference;
		final Context context;

		InferenceWrapper(TracedConclusion inf, Context cxt) {
			inference = inf;
			context = cxt;
		}

		@Override
		public String toString() {
			return inference + " stored in " + context;
		}

	}
}
