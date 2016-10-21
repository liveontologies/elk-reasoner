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
package org.semanticweb.elk.loading;

import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.concurrent.computation.DelegateInterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * A skeletal implementation of the {@link AxiomLoader} that minimizes the
 * effort to implement the interface.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractAxiomLoader extends DelegateInterruptMonitor implements
		AxiomLoader {

	public AbstractAxiomLoader(final InterruptMonitor interrupter) {
		super(interrupter);
	}

	@Override
	public abstract void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException;

	@Override
	public abstract boolean isLoadingFinished();

	@Override
	public void dispose() {
		// does nothing
	}

}
