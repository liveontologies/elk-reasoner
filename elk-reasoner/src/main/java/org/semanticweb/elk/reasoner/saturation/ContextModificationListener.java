/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;
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

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextModificationListener {

	/**
	 * Invoked right after the context has been marked as saturated or not
	 * saturated
	 * 
	 * @param context
	 *            the {@link Context} whose saturation flag has changed
	 */ 
	public void notifyContextModification(Context context);
	
	public static final ContextModificationListener DUMMY = new ContextModificationListener() {
		
		@Override
		public void notifyContextModification(Context context) {
			//doesn't do anything
		}
	};
}
