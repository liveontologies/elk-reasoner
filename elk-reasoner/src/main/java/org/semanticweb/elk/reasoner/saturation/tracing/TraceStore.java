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

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * The main object responsible for storing and retrieving traces.
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
		 * 
		 * @param context
		 * @param conclusion
		 * @param visitor
		 */
		public void accept(Context context, Conclusion conclusion, TracedConclusionVisitor<?,?> visitor);
		
		//public Iterable<Context> getContexts();
		
		//public void visitConclusions(Context context, ConclusionVisitor<?, ?> visitor);
		
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
		public boolean addInference(Context context, TracedConclusion conclusion);
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
