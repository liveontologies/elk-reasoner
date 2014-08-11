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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;

/**
 * Recursively visits all conclusions which were used to produce a given
 * conclusion. It can notify the caller if some visited conclusion has not been
 * traced (this is useful for testing). 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestTraceUnwinder implements TraceUnwinder {

	private final TraceStore.Reader traceReader_;

	private final UntracedConclusionListener listener_;

	private final static ClassInferenceVisitor<IndexedClassExpression, ?> DUMMY_INFERENCE_VISITOR = new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {
		@Override
		protected Void defaultTracedVisit(ClassInference conclusion,
				IndexedClassExpression input) {
			return null;
		}
	};

	public TestTraceUnwinder(TraceStore.Reader reader) {
		this(reader, UntracedConclusionListener.DUMMY);
	}

	TestTraceUnwinder(TraceStore.Reader reader,
			UntracedConclusionListener listener) {
		traceReader_ = reader;
		listener_ = listener;
	}

	public void accept(IndexedClassExpression context,
			final Conclusion conclusion,
			final ConclusionVisitor<IndexedClassExpression, ?> premiseVisitor) {
		accept(context, conclusion, premiseVisitor, DUMMY_INFERENCE_VISITOR, ObjectPropertyConclusionVisitor.DUMMY, ObjectPropertyInferenceVisitor.DUMMY);
	}
	
	// unwind only property conclusions
	@Override
	public void accept(ObjectPropertyConclusion conclusion,
			final ObjectPropertyConclusionVisitor<?, ?> premiseVisitor,
			final ObjectPropertyInferenceVisitor<?, ?> inferenceVisitor) {
		final Queue<ObjectPropertyInference> toDo = new LinkedList<ObjectPropertyInference>();
		final Set<ObjectPropertyInference> seenInferences = new HashSet<ObjectPropertyInference>();

		addToQueue(conclusion, toDo, seenInferences, premiseVisitor);
		// set it off
		unwindPropertyConclusions(toDo, premiseVisitor, inferenceVisitor);		
	}
	
	private void unwindPropertyConclusions(final Queue<ObjectPropertyInference> toDo,
			final ObjectPropertyConclusionVisitor<?, ?> premiseVisitor,
			final ObjectPropertyInferenceVisitor<?, ?> inferenceVisitor) {
		final Set<ObjectPropertyInference> seenInferences = new HashSet<ObjectPropertyInference>();

		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<Void, ?> unwinder = new PremiseVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(ObjectPropertyConclusion premise) {
				addToQueue(premise, toDo, seenInferences, premiseVisitor);
				return null;
			}
		};

		for (;;) {
			final ObjectPropertyInference next = toDo.poll();

			if (next == null) {
				break;
			}

			next.acceptTraced(inferenceVisitor, null);
			next.acceptTraced(unwinder, null);
		}		
	}

	/**
	 * Unwinds both class and property conclusions
	 * 
	 * @param context
	 * @param conclusion
	 * @param classConclusionVisitor
	 *            Visitor over all conclusions which were used as premises
	 * @param inferenceVisitor
	 *            Visitor over all
	 */
	@Override
	public void accept(
			final IndexedClassExpression context,
			final Conclusion conclusion,
			final ConclusionVisitor<IndexedClassExpression, ?> classConclusionVisitor,
			final ClassInferenceVisitor<IndexedClassExpression, ?> inferenceVisitor,
			final ObjectPropertyConclusionVisitor<?, ?> propertyConclusionVisitor,
			final ObjectPropertyInferenceVisitor<?, ?> propertyInferenceVisitor) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Queue<ObjectPropertyInference> propertyInferenceToDo = new LinkedList<ObjectPropertyInference>();
		final Set<ClassInference> seenInferences = new HashSet<ClassInference>();
		final Set<ObjectPropertyInference> seenPropertyInferences = new HashSet<ObjectPropertyInference>();

		addToQueue(context, conclusion, toDo, seenInferences, classConclusionVisitor);
		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<IndexedClassExpression, ?> premiseVisitor = new PremiseVisitor<IndexedClassExpression, Void>() {

			@Override
			protected Void defaultVisit(Conclusion premise,
					IndexedClassExpression cxt) {
				// the context passed into this method is the context where the
				// inference has been made
				addToQueue(cxt, premise, toDo, seenInferences, classConclusionVisitor);
				return null;
			}

			@Override
			protected Void defaultVisit(ObjectPropertyConclusion premise) {
				addToQueue(premise, propertyInferenceToDo, seenPropertyInferences, propertyConclusionVisitor);
				
				return null;
			}
			
			
		};

		for (;;) {
			final InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}

			next.inference.acceptTraced(inferenceVisitor, null);
			next.inference.acceptTraced(premiseVisitor, next.contextRoot);
		}
		
		// finally, unwind all property traces
		unwindPropertyConclusions(propertyInferenceToDo, propertyConclusionVisitor, propertyInferenceVisitor);
	}

	private void addToQueue(final IndexedClassExpression root,
			final Conclusion conclusion, final Queue<InferenceWrapper> toDo,
			final Set<ClassInference> seenInferences,
			final ConclusionVisitor<IndexedClassExpression, ?> visitor) {

		conclusion.accept(visitor, root);

		final MutableInteger traced = new MutableInteger(0);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(root, conclusion,
				new AbstractClassInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(ClassInference inference,
							Void v) {
						if (!seenInferences.contains(inference)) {
							IndexedClassExpression inferenceContextRoot = inference
									.getInferenceContextRoot(root);

							seenInferences.add(inference);
							toDo.add(new InferenceWrapper(inference,
									inferenceContextRoot));
						}

						traced.increment();

						return null;
					}

				});

		if (traced.get() == 0) {
			listener_.notifyUntraced(conclusion, root);
		}
	}
	
	private void addToQueue(final ObjectPropertyConclusion conclusion, final Queue<ObjectPropertyInference> toDo,
			final Set<ObjectPropertyInference> seenInferences,
			final ObjectPropertyConclusionVisitor<?, ?> visitor) {

		conclusion.accept(visitor, null);
		
		final MutableInteger traced = new MutableInteger(0);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(conclusion,
				new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(ObjectPropertyInference inference, Void _ignored) {
						if (!seenInferences.contains(inference)) {
							seenInferences.add(inference);
							
							toDo.add(inference);
						}

						traced.increment();

						return null;
					}

				});

		if (traced.get() == 0) {
			listener_.notifyUntraced(conclusion);
		}
	}	

	/*
	 * Used to propagate context which normally isn't stored inside each traced
	 * conclusion
	 */
	private static class InferenceWrapper {

		final ClassInference inference;
		final IndexedClassExpression contextRoot;

		InferenceWrapper(ClassInference inf, IndexedClassExpression root) {
			inference = inf;
			contextRoot = root;
		}

		@Override
		public String toString() {
			return inference + " stored in " + contextRoot;
		}

	}

}
