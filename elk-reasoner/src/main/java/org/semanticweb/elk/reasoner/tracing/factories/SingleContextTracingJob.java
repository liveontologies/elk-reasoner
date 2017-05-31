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
package org.semanticweb.elk.reasoner.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;

/**
 * A job for computing applied
 * {@link org.semanticweb.elk.reasoner.saturation.inferences.ClassInference
 * ClassInference}s with the {@link IndexedContextRoot} origin that is
 * {@link ClassConclusion#getTraceRoot()} of the provided
 * {@link ClassConclusion}. Intended to be used with
 * {@link SingleContextTracingFactory}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
class SingleContextTracingJob extends SaturationJob<IndexedContextRoot> {

	private final ClassConclusion goalConclusion_;

	public SingleContextTracingJob(final ClassConclusion goalConclusion) {
		super(goalConclusion.getTraceRoot());
		this.goalConclusion_ = goalConclusion;
	}

	ClassConclusion getGoalConclusion() {
		return goalConclusion_;
	}

}
