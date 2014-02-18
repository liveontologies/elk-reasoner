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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * The main object responsible for storing and retrieving inferences for {@link Conclusion}s.
 * Inferences are represented using {@link Inference}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface TraceStore {

	/**
	 * 
	 */
	public interface Reader {

		/**
		 * TODO let the calling code stop the visiting process, for example, by returning false from the visitor.
		 * 
		 * @param context
		 * @param conclusion
		 * @param visitor
		 */
		public void accept(IndexedClassExpression root, Conclusion conclusion, InferenceVisitor<?,?> visitor);
		
		public Iterable<IndexedClassExpression> getContextRoots();
		
		public void visitInferences(IndexedClassExpression root, InferenceVisitor<?, ?> visitor);
	}

	/**
	 * 
	 */
	public interface Writer {
		
		/**
		 * 
		 * @param context
		 * @param conclusion
		 * @param inference
		 * @return
		 */
		public boolean addInference(IndexedClassExpression root, Inference conclusion);
	}

	/**
	 * 
	 * @return
	 */
	public Reader getReader();

	/**
	 * 
	 * @return
	 */
	public Writer getWriter();
}
