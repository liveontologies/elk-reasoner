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

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.owl.exceptions.ElkException;

/**
 * A {@link ReasonerStage} during which the input ontology is loaded into the
 * reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OntologyLoadingStage extends AbstractReasonerStage {

	public OntologyLoadingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(OntologyLoadingStage.class);

	private Loader ontologyLoader_;

	@Override
	public String getName() {
		return "Loading of Axioms";
	}

	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		ontologyLoader_ = reasoner.getOntologyLoader();
		reasoner.trySetIncrementalMode(false);
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		if (this.ontologyLoader_ == null)
			LOGGER_.warn("Ontology loader is not registered. No axioms will be loaded!");
		else
			for (;;) {
				this.ontologyLoader_.load();
				if (!spuriousInterrupt())
					break;
			}
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute())
			return false;		
		this.ontologyLoader_ = null;
		return true;
	}

	@Override
	public void printInfo() {
	}

	// ///////////////////////////////////////////////////////////////////////////////
	/*
	 * POST PROCESSING, FOR DEBUGGING ONLY
	 */
	// ////////////////////////////////////////////////////////////////////////////////

	/*
	 * @Override public Collection<ReasonerStage> getPostProcessingStages() {
	 * return Arrays .<ReasonerStage> asList(new
	 * SaturatedPropertyChainCheckingStage( reasoner.ontologyIndex)); }
	 */

}
