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

import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A conclusion visitor which processes {@link Inference}s and saves
 * their inferences using a {@link TraceStore.Writer}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceInsertionVisitor extends BaseConclusionVisitor<Boolean, Context> {

	protected static final Logger LOGGER_ = LoggerFactory.getLogger(InferenceInsertionVisitor.class);
	
	private final TraceStore.Writer traceWriter_;

	private final InferenceVisitor<Boolean, Context> tracedVisitor_ = new BaseInferenceVisitor<Boolean, Context>() {

		@Override
		protected Boolean defaultTracedVisit(Inference conclusion, Context context) {
			traceWriter_.addInference(context.getRoot(), conclusion);
			
			return true;
		}
	};

	/**
	 * 
	 */
	public InferenceInsertionVisitor(TraceStore.Writer traceWriter) {
		traceWriter_ = traceWriter;
	}

	protected InferenceVisitor<Boolean, Context> getTracedConclusionVisitor() {
		return tracedVisitor_;
	}
	
	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
		if (conclusion instanceof Inference) {
			return ((Inference) conclusion).acceptTraced(getTracedConclusionVisitor(), cxt);	
		}
		else {
			LOGGER_.warn("Tracing is ON but {} is not traced", conclusion);
			
			return true;
		}
	}

}
