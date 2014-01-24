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

import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link TraceStore} which uses centralized concurrent data structures to
 * store and retrieve {@link Inference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SimpleCentralizedTraceStore implements TraceStore {
	
	private final static Logger LOGGER_ = LoggerFactory.getLogger(SimpleCentralizedTraceStore.class);	
	//TODO: use context roots as keys instead
	private final ConcurrentHashMap<Context, ContextTraceStore> storage_ = new ConcurrentHashMap<Context, ContextTraceStore>();
	
	@Override
	public TraceStore.Reader getReader() {
		return new Reader();
	}

	@Override
	public TraceStore.Writer getWriter() {
		return new Writer();
	}
	
	/**
	 * 
	 *
	*/
	private class Reader implements TraceStore.Reader {

		@Override
		public void accept(Context context, Conclusion conclusion, TracedConclusionVisitor<?,?> visitor) {
			ContextTraceStore tracer = storage_.get(context);
			
			if (tracer != null) {
				tracer.accept(conclusion, visitor);
			}
		}

		/*@Override
		public Iterable<Context> getContexts() {
			return storage_.keySet();
		}

		@Override
		public void visitConclusions(Context context, ConclusionVisitor<?, ?> visitor) {
			ContextTracer tracer = storage_.get(context);
			
			if (tracer != null) {
				tracer.visitConclusions(visitor);
			}
		}*/
		
	}
	
	private class Writer implements TraceStore.Writer {

		@Override
		public boolean addInference(Context context, TracedConclusion conclusion) {
			ContextTraceStore tracer = storage_.get(context);
			
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Writing inference for {} in {}: {}", conclusion, context, conclusion.acceptTraced(new InferencePrinter(), null));
			}
			
			if (tracer == null) {
				tracer = new SimpleContextTraceStore();
				storage_.putIfAbsent(context, tracer);
			}
			
			return tracer.addInference(conclusion);
		}
		
	}

}
