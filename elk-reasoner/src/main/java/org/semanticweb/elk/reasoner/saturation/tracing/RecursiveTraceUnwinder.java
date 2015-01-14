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
import java.util.Set;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractObjectPropertyConclusionVIsitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;

/**
 * Recursively visits inferences which were used to produce a given conclusion.
 * It stops when the inference visitors passed by the outer code returns
 * {@code false} for a particular inference.
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveTraceUnwinder implements TraceUnwinder<Boolean> {

	private final TraceStore.Reader traceReader_;
	
	private final LinkedList<InferenceWrapper> classInferencesToDo_ = new LinkedList<InferenceWrapper>();
	
	private final LinkedList<ObjectPropertyInference> propertyInferencesToDo_ = new LinkedList<ObjectPropertyInference>();

	public RecursiveTraceUnwinder(TraceStore.Reader reader) {
		traceReader_ = reader;
	}

	// visit only class inferences
	public void accept(IndexedClassExpression context,
			final Conclusion conclusion,
			final ClassInferenceVisitor<IndexedClassExpression, Boolean> inferenceVisitor) {
		accept(context, conclusion, inferenceVisitor, new AbstractObjectPropertyInferenceVisitor<Void, Boolean>() {

			@Override
			protected Boolean defaultTracedVisit(ObjectPropertyInference inference, Void input) {
				return true;
			}
			
		});
	}	
	
	// unwind only property conclusions
	@Override
	public void accept(ObjectPropertyConclusion conclusion,
			final ObjectPropertyInferenceVisitor<?, Boolean> inferenceVisitor) {
		final Set<ObjectPropertyInference> seenInferences = new HashSet<ObjectPropertyInference>();

		addToQueue(conclusion, seenInferences);
		// set it off
		unwindPropertyConclusions(inferenceVisitor);
	}
	
	private void unwindPropertyConclusions(
			final ObjectPropertyInferenceVisitor<?, Boolean> inferenceVisitor) {
		final Set<ObjectPropertyInference> seenInferences = new HashSet<ObjectPropertyInference>();

		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<Void, ?> unwinder = new PremiseVisitor<Void, Void>(new AbstractObjectPropertyConclusionVIsitor<Void, Void>() {
			@Override
			protected Void defaultVisit(ObjectPropertyConclusion premise, Void _ignored) {
				addToQueue(premise, seenInferences);
				return null;
			}
		});

		for (;;) {
			final ObjectPropertyInference next = propertyInferencesToDo_.poll();

			if (next == null) {
				break;
			}

			if (next.acceptTraced(inferenceVisitor, null)) {
				// unwind premises unless we're told to stop the recursion
				next.acceptTraced(unwinder, null);
			}
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
			final ClassInferenceVisitor<IndexedClassExpression, Boolean> inferenceVisitor,
			final ObjectPropertyInferenceVisitor<?, Boolean> propertyInferenceVisitor) {
		final Set<ClassInference> seenInferences = new HashSet<ClassInference>();
		final Set<ObjectPropertyInference> seenPropertyInferences = new HashSet<ObjectPropertyInference>();
		// should be empty anyways
		classInferencesToDo_.clear();
		addToQueue(context, conclusion, seenInferences);
		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<IndexedClassExpression, ?> premiseVisitor = 
				new PremiseVisitor<IndexedClassExpression, Void>(
						new AbstractConclusionVisitor<IndexedClassExpression, Void>() {
							@Override
							protected Void defaultVisit(Conclusion premise,
									IndexedClassExpression cxt) {
								// the context passed into this method is the context where the
								// inference has been made
								addToQueue(cxt, premise, seenInferences);
								return null;
							}
						},
						new AbstractObjectPropertyConclusionVIsitor<IndexedClassExpression, Void>() {
							@Override
							protected Void defaultVisit(ObjectPropertyConclusion premise, IndexedClassExpression _ignored) {
								addToQueue(premise, seenPropertyInferences);
								
								return null;
							}
						});

		for (;;) {
			// take the first element
			final InferenceWrapper next = classInferencesToDo_.poll();

			if (next == null) {
				break;
			}
			// user visitor
			if (next.inference.acceptTraced(inferenceVisitor, next.contextRoot)) {
				// visiting premises
				next.inference.acceptTraced(premiseVisitor, next.inference.getInferenceContextRoot(next.contextRoot));
			}
		}
		
		// finally, unwind all property traces
		unwindPropertyConclusions(propertyInferenceVisitor);
	}

	private void addToQueue(final IndexedClassExpression root,
			final Conclusion conclusion, 
			final Set<ClassInference> seenInferences) {

		final MutableInteger traced = new MutableInteger(0);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(root, conclusion,
				new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

					@Override
					protected Void defaultTracedVisit(ClassInference inference,
							IndexedClassExpression v) {
						if (!seenInferences.contains(inference)) {

							seenInferences.add(inference);
							classInferencesToDo_.addFirst(new InferenceWrapper(inference, root));
						}

						traced.increment();

						return null;
					}

				});

		if (traced.get() == 0) {
			handleUntraced(conclusion, root);
		}
	}
	
	private void addToQueue(final ObjectPropertyConclusion conclusion,
			final Set<ObjectPropertyInference> seenInferences) {

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
							
							propertyInferencesToDo_.add(inference);
						}

						traced.increment();

						return null;
					}

				});

		if (traced.get() == 0) {
			handleUntraced(conclusion);
		}
	}	

	protected void handleUntraced(Conclusion untraced, IndexedClassExpression root) {
		//no-op
	}
	
	protected void handleUntraced(ObjectPropertyConclusion untraced) {
		//no-op
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
