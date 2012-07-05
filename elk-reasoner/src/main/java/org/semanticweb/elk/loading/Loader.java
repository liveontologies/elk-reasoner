/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.loading;

/**
 * A common interface for loading processes
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Loader {

	/**
	 * Runs the loading process. If loading process is interrupted, it can be
	 * continued by calling this method again.
	 * 
	 * @throws ElkLoadingException
	 *             if loading cannot be completed successfully
	 */
	public void load() throws ElkLoadingException;

}
