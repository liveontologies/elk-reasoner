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

import org.semanticweb.elk.owl.visitors.ElkClassExpressionProcessor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * An object through which class expression queries can be added to or removed
 * from the index.
 * 
 * @author Peter Skocovsky
 */
public interface ClassQueryLoader extends InterruptMonitor {

	/**
	 * Loads pending queries using the provided {@code  inserter} for inserting
	 * queries and {@code deleter} for deleting queries; if called twice, the
	 * already loaded axioms will not be processed again.
	 * 
	 * @param inserter
	 *            an {@link ElkClassExpressionProcessor} that inserts the
	 *            queries
	 * @param deleter
	 *            an {@link ElkClassExpressionProcessor} that deletes the
	 *            queries
	 * @throws ElkLoadingException
	 *             if loading cannot be completed successfully
	 */
	void load(ElkClassExpressionProcessor inserter,
			ElkClassExpressionProcessor deleter) throws ElkLoadingException;

	/**
	 * @return {@code true} if the loading is finished, i.e., calling
	 *         {@link ClassQueryLoader#load(ElkClassExpressionProcessor, ElkClassExpressionProcessor)}
	 *         will have no effect
	 */
	public boolean isLoadingFinished();

	/**
	 * Close resources used by this {@link ClassQueryLoader}
	 */
	public void dispose();

	public static interface Factory {

		ClassQueryLoader getQueryLoader(InterruptMonitor interrupter);

	}

}
