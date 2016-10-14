package org.semanticweb.elk.loading;

import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

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

/**
 * A {@link AxiomLoader} that consists of two given {@link AxiomLoader}s. When
 * changes are loaded, they are first loaded using the first {@link AxiomLoader}
 * , and then using the second one.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ComposedAxiomLoader extends SimpleInterrupter implements
		AxiomLoader {

	private final AxiomLoader firstLoader_, secondLoader_;

	public ComposedAxiomLoader(AxiomLoader firstLoader, AxiomLoader secondLoader) {
		this.firstLoader_ = firstLoader;
		this.secondLoader_ = secondLoader;
	}

	@Override
	public void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {
		if (!firstLoader_.isLoadingFinished()) {
			firstLoader_.load(axiomInserter, axiomDeleter);
		}	
		if (firstLoader_.isLoadingFinished()
				&& !secondLoader_.isLoadingFinished()) {
			secondLoader_.load(axiomInserter, axiomDeleter);
		}
	}

	@Override
	public void dispose() {
		firstLoader_.dispose();
		secondLoader_.dispose();
	}

	@Override
	public boolean isLoadingFinished() {
		return firstLoader_.isLoadingFinished()
				&& secondLoader_.isLoadingFinished();
	}

	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		firstLoader_.setInterrupt(flag);
		secondLoader_.setInterrupt(flag);
	}

}
