package org.semanticweb.elk.reasoner.saturation.inferences;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.semanticweb.elk.reasoner.tracing.TracingInferencePrinter;

/**
 * A skeleton implementation of {@link ClassInference}
 * 
 * @author Yevgeny Kazakov
 *
 */
abstract class AbstractClassInference implements ClassInference {

	private final IndexedContextRoot destination_;

	protected AbstractClassInference(IndexedContextRoot destination) {
		this.destination_ = destination;
	}

	@Override
	public final IndexedContextRoot getDestination() {
		return this.destination_;
	}

	@Override
	public final IndexedContextRoot getTraceRoot() {
		return this.destination_;
	}

	// we assume that different objects represent different inferences
	
	@Override
	public final String toString() {
		return TracingInferencePrinter.toString(this);		
	}

	@Override
	public final <O> O accept(TracingInference.Visitor<O> visitor) {
		return accept((ClassInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SaturationInference.Visitor<O> visitor) {
		return accept((ClassInference.Visitor<O>) visitor);
	}

}
