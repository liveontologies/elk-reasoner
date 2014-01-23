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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * The extended writer for situations when new contexts may need to be
 * created/initialized or removed. With every
 * {@link ExtendedSaturationStateWriter} one can register a
 * {@link ContextCreationListener} that will be executed every time this
 * {@link ExtendedSaturationStateWriter} creates a new {@code Context}. Although
 * all functions of this {@link ExtendedSaturationStateWriter} are thread safe,
 * the function of the {@link ContextCreationListener} might not be, in which
 * the access of functions of {@link ExtendedSaturationStateWriter} should be
 * synchronized between threads.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ExtendedSaturationStateWriter extends
		BasicSaturationStateWriter {

	public Context getCreateContext(IndexedClassExpression root);
	/*
	 * TODO probably isn't needed since intialization is done manually
	 * 
	 * It was introduced to let writer wrappers (e.g., the tracing writer)
	 * propagate its conclusion factory to the underlying writer. It was
	 * necessary because otherwise the wrapper won't be able to make sure that
	 * its factory will be used when a context is initialized since the
	 * initContext() method is called directly by the underlying writer from inside of
	 * getCreateContext().
	 */
	//public Context getCreateContext(IndexedClassExpression root, ConclusionFactory factory);

	public void initContext(Context context);
	
}
