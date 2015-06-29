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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.InferencePrinter;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple {@link TraceStore} which uses a centralized concurrent data
 * structure to store and retrieve {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SimpleCentralizedTraceStore implements TraceStore {

	private final static Logger LOGGER_ = LoggerFactory
			.getLogger(SimpleCentralizedTraceStore.class);

	private final ConcurrentHashMap<IndexedContextRoot, ContextTraceStore> storage_ = new ConcurrentHashMap<IndexedContextRoot, ContextTraceStore>();

	private final ObjectPropertyInferenceStore propertyInferenceStore_ = new SimpleObjectPropertyInferenceStore();

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
		public void accept(Conclusion conclusion,
				ClassInferenceVisitor<IndexedContextRoot, ?> visitor) {
			ContextTraceStore tracer = storage_.get(conclusion.getConclusionRoot());

			if (tracer != null) {
				tracer.accept(conclusion, visitor);
			}
		}

		@Override
		public Iterable<IndexedContextRoot> getContextRoots() {
			return storage_.keySet();
		}

		@Override
		public void visitInferences(IndexedContextRoot root,
				ClassInferenceVisitor<IndexedContextRoot, ?> visitor) {
			ContextTraceStore tracer = storage_.get(root);

			if (tracer != null) {
				tracer.visitInferences(visitor);
			}
		}

		@Override
		public void accept(ObjectPropertyConclusion conclusion,
				ObjectPropertyInferenceVisitor<?, ?> visitor) {
			propertyInferenceStore_.visitInferences(conclusion, visitor);
		}

		@Override
		public void visitInferences(IndexedPropertyChain ipc,
				ObjectPropertyInferenceVisitor<?, ?> visitor) {
			propertyInferenceStore_.visitInferences(ipc, visitor);
		}

	}

	private class Writer implements TraceStore.Writer {

		@Override
		public boolean addInference(IndexedContextRoot root,
				ClassInference conclusion) {
			ContextTraceStore tracer = storage_.get(root);

			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Writing inference for {} in {}: {}", conclusion,
						root,
						conclusion.acceptTraced(new InferencePrinter(), null));
			}

			if (tracer == null) {
				tracer = new SimpleContextTraceStore(root);
				storage_.putIfAbsent(root, tracer);
			}

			return tracer.addInference(conclusion);
		}

		@Override
		public boolean addObjectPropertyInference(
				ObjectPropertyInference conclusion) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Writing property inference {}",
						conclusion.acceptTraced(new InferencePrinter(), null));
			}

			return propertyInferenceStore_.addInference(conclusion);
		}

	}

	@Override
	public void cleanClassInferences() {
		storage_.clear();
	}

	@Override
	public void cleanObjectPropertyInferences() {
		propertyInferenceStore_.clear();
	}

}
