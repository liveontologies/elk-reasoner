/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A conclusion visitor which saves inferences using a {@link TraceStore.Writer}
 * .
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceInsertionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(InferenceInsertionVisitor.class);

	private final TraceStore.Writer traceWriter_;

	private final ClassInferenceVisitor<Context, Boolean> inferenceInserter_ = new AbstractClassInferenceVisitor<Context, Boolean>() {

		@Override
		protected Boolean defaultTracedVisit(ClassInference inference,
				Context context) {
			traceWriter_.addClassInference(inference);

			return true;
		}
	};

	/**
	 * 
	 */
	public InferenceInsertionVisitor(TraceStore.Writer traceWriter) {
		traceWriter_ = traceWriter;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
		if (conclusion instanceof ClassInference) {
			return ((ClassInference) conclusion).acceptTraced(
					inferenceInserter_, cxt);
		}
		LOGGER_.trace(
				"Tracing is ON but {} does not contain tracing information",
				conclusion);

		return true;
	}

}
