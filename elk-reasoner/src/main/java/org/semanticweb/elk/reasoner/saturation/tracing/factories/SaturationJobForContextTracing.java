package org.semanticweb.elk.reasoner.saturation.tracing.factories;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;

class SaturationJobForContextTracing<I extends IndexedContextRoot, J extends ContextTracingJob<I>>
		extends SaturationJob<I> {

	private final J initiatorJob_;

	public SaturationJobForContextTracing(J initiatorJob) {
		super(initiatorJob.getInput());
		this.initiatorJob_ = initiatorJob;
	}

	J getInitiatorJob() {
		return this.initiatorJob_;
	}

}
