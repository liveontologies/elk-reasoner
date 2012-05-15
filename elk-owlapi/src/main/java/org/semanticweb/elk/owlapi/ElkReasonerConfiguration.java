/*
 * #%L
 * ELK OWL API Binding
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
/**
 * 
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * A composition of the generic OWL API configuration and the ELK's configuration
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkReasonerConfiguration implements OWLReasonerConfiguration {

	private final ReasonerConfiguration elkConfig;
	private final OWLReasonerConfiguration genericConfig;
	
	public ElkReasonerConfiguration(OWLReasonerConfiguration genConfig) {
		elkConfig = ReasonerConfiguration.getDefaultConfiguration();
		genericConfig = genConfig;
	}
	
	public ElkReasonerConfiguration(OWLReasonerConfiguration genConfig, ReasonerConfiguration elkConfig) {
		this.elkConfig = elkConfig;
		genericConfig = genConfig;
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return genericConfig.getFreshEntityPolicy();
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return genericConfig.getIndividualNodeSetPolicy();
	}

	@Override
	public ReasonerProgressMonitor getProgressMonitor() {
		return genericConfig.getProgressMonitor();
	}

	@Override
	public long getTimeOut() {
		return genericConfig.getTimeOut();
	}
	

	public ReasonerConfiguration getElkConfiguration() {
		return elkConfig;
	}
}
