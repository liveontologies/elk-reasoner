package org.semanticweb.elk.owl.managers;

/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Interface for classes using which one can reuse already created
 * {@link ElkObject}s.
 * 
 * @author Markus Kroetzsch
 * @author "Yevgeny Kazakov"
 */
public interface ElkObjectRecycler {

	/**
	 * Process the given {@link ElkObject} and either return itself, or another
	 * (previously processed) {@link ElkObject} that is structurally equivalent
	 * to the given one.
	 * 
	 * @param object
	 *            the input {@link ElkObject} which should be processed
	 * @return an {@link ElkObject} that is structurally equivalent to the given
	 *         object
	 */
	public ElkObject recycle(ElkObject object);

}
