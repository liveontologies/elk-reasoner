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
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.owl.exceptions.ElkException;

/**
 * A {@link ReasonerStage} during which ontology changes are applied to the
 * reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ChangesLoadingStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ChangesLoadingStage.class);

	public ChangesLoadingStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return "Loading of Changes";
	}

	@Override
	public boolean done() {
		return reasoner.doneChangeLoading;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Arrays.asList(manager.ontologyLoadingStage);
	}

	@Override
	public void executeStage() throws ElkException {
		Loader changesLoader = reasoner.getChangesLoader();
		if (changesLoader == null)
			LOGGER_.warn("Ontology changes loader is not registered. No changes will be loaded!");
		else {
			try {
				for (;;) {
					changesLoader.load();
					if (!interrupted())
						break;
				}
			} finally {
				changesLoader.dispose();
			}
		}
		reasoner.doneChangeLoading = true;
	}

	@Override
	public void printInfo() {
	}

}
