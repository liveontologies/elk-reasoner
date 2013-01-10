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
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.stages.debug.PostProcessingReasonerStage;
import org.semanticweb.elk.reasoner.stages.debug.SaturatedPropertyChainCheckingStage;

/**
 * A {@link ReasonerStage} during which the input ontology is loaded into the
 * reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OntologyLoadingStage extends AbstractReasonerStage implements
		PostProcessingReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(OntologyLoadingStage.class);

	public OntologyLoadingStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Loading of Axioms";
	}

	@Override
	public boolean done() {
		return reasoner.doneLoading;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void execute() throws ElkException {
		initComputation();
		Loader ontologyLoader = reasoner.getOntologyLoader();
		if (ontologyLoader == null)
			LOGGER_.warn("Ontology loader is not registered. No axioms will be loaded!");
		else
			for (;;) {
				ontologyLoader.load();
				if (!interrupted())
					break;
			}
		reasoner.doneLoading = true;
	}

	@Override
	public void printInfo() {
	}

	// ///////////////////////////////////////////////////////////////////////////////
	/*
	 * POST PROCESSING, FOR DEBUGGING ONLY
	 */
	// ////////////////////////////////////////////////////////////////////////////////

	@Override
	public Collection<ReasonerStage> getPostProcessingStages() {
		return Arrays
				.<ReasonerStage> asList(new SaturatedPropertyChainCheckingStage(
						reasoner.ontologyIndex));
	}

}
