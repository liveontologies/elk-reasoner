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

import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * An object through which axioms can be add or removed to the ontology
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public interface AxiomLoader extends InterruptMonitor {

	/**
	 * Loads pending axioms using the provided {@code  axiomInserter} for
	 * inserting axioms and {@code axiomDeleter} for deleting axioms; if called
	 * twice, the already loaded axioms will not be processed again
	 * 
	 * @param axiomInserter
	 *            an {@link ElkAxiomProcessor} that inserts the axioms that were
	 *            added
	 * @param axiomDeleter
	 *            an {@link ElkAxiomProcessor} that deletes the axioms that were
	 *            removed
	 * @throws ElkLoadingException
	 *             if loading cannot be completed successfully
	 */
	public void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException;

	/**
	 * @return {@code true} if the loading is finished, i.e., calling
	 *         {@link AxiomLoader#load(ElkAxiomProcessor, ElkAxiomProcessor)}
	 *         will have no effect
	 */
	public boolean isLoadingFinished();

	/**
	 * Close resources used by this {@link AxiomLoader}
	 */
	public void dispose();

	public static interface Factory {

		AxiomLoader getAxiomLoader(InterruptMonitor interrupter);

	}

}
