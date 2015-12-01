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

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInferencePremiseVisitor;

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

	private final InferenceSet inferenceSet_;

	private final LinkedList<SaturationInference> innferencesToDo_ = new LinkedList<SaturationInference>();

	public RecursiveTraceUnwinder(InferenceSet inferenceSet) {
		this.inferenceSet_ = inferenceSet;
	}

	/**
	 * Unwinds saturation conclusions
	 * 
	 * @param conclusion
	 * @param classConclusionVisitor
	 *            Visitor over all conclusions which were used as premises
	 * @param inferenceVisitor
	 *            Visitor over all
	 */
	@Override
	public void accept(final SaturationConclusion conclusion,
			final SaturationInference.Visitor<Boolean> inferenceVisitor) {
		final Set<SaturationInference> seenInferences = new HashSet<SaturationInference>();
		// should be empty anyways
		innferencesToDo_.clear();
		addToQueue(conclusion, seenInferences);
		// this visitor visits all premises and putting them into the todo queue
		SaturationInferencePremiseVisitor<?> premiseVisitor = new SaturationInferencePremiseVisitor<Void>(
				new SaturationConclusionBaseFactory() {
					@Override
					protected <C extends SaturationConclusion> C filter(
							C newConclusion) {
						addToQueue(newConclusion, seenInferences);
						return newConclusion;
					}

				});

		for (;;) {
			// take the first element
			final SaturationInference next = innferencesToDo_.poll();

			if (next == null) {
				break;
			}
			// user visitor
			if (next.accept(inferenceVisitor)) {
				// visiting premises
				next.accept(premiseVisitor);
			}
		}

	}

	private void addToQueue(final SaturationConclusion conclusion,
			final Set<SaturationInference> seenInferences) {

		boolean derived = false;
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		for (SaturationInference inference : inferenceSet_
				.getInferences(conclusion)) {
			if (!seenInferences.contains(inference)) {
				seenInferences.add(inference);
				innferencesToDo_.addFirst(inference);
			}
			derived = true;
		}

		if (!derived) {
			handleUntraced(conclusion);
		}
	}

	protected void handleUntraced(
			@SuppressWarnings("unused") SaturationConclusion untraced) {
		// no-op
	}

}
