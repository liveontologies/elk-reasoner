package org.semanticweb.elk.benchmark.tracing;
/*
 * #%L
 * ELK Benchmarking Package
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractInferenceVisitor;

/**
 * Counts the number of visited inferences (instances of {@link Inference}).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class UsedInferencesCounter extends
		AbstractInferenceVisitor<IndexedClassExpression, Void> {

	private int infCounter_ = 0;

	// private Set<TracedConclusion> inferences = new
	// HashSet<TracedConclusion>();

	@Override
	protected Void defaultTracedVisit(Inference conclusion,
			IndexedClassExpression parameter) {
		infCounter_++;
		// inferences.add(conclusion);
		// System.out.println(parameter + ": " + conclusion + ": " +
		// InferencePrinter.print(conclusion));

		return null;
	}

	public void resetCounter() {
		infCounter_ = 0;
	}

	public int getInferenceCount() {
		return infCounter_;
		// return inferences.size();
	}

}