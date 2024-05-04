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
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

/**
 * A composition of the generic OWL API configuration and the ELK's
 * configuration. This is also where the default configuration settings of OWL
 * API reasoner parameters in ELK are defined. The default configuration of ELK
 * reasoner parameters is in {@link ReasonerConfiguration}.
 * 
 * @author Pavel Klinov pavel.klinov@uni-ulm.de
 * @author Markus Kroetzsch
 */
public class ElkReasonerConfiguration implements OWLReasonerConfiguration {

	private static final long serialVersionUID = 4747024112171682291L;	
	private final ReasonerConfiguration elkConfig;
	private final OWLReasonerConfiguration owlConfig;

	public ElkReasonerConfiguration(OWLReasonerConfiguration owlConfig,
			ReasonerConfiguration elkConfig) {
		this.elkConfig = elkConfig;
		this.owlConfig = owlConfig;
	}

	public ElkReasonerConfiguration() {
		this(getDefaultOwlReasonerConfiguration(), ReasonerConfiguration
				.getConfiguration());
	}

	public ElkReasonerConfiguration(ReasonerProgressMonitor monitor) {
		this(getDefaultOwlReasonerConfiguration(monitor), ReasonerConfiguration
				.getConfiguration());
	}

	public ElkReasonerConfiguration(OWLReasonerConfiguration genConfig) {
		this(genConfig, ReasonerConfiguration.getConfiguration());
	}

	public static OWLReasonerConfiguration getDefaultOwlReasonerConfiguration(
			ReasonerProgressMonitor monitor) {
		return new SimpleConfiguration(monitor, FreshEntityPolicy.ALLOW, 0,
				IndividualNodeSetPolicy.BY_NAME);
	}
	
	public static OWLReasonerConfiguration getDefaultOwlReasonerConfiguration() {
		return getDefaultOwlReasonerConfiguration(
				new NullReasonerProgressMonitor());
	}

	@Override
	public FreshEntityPolicy getFreshEntityPolicy() {
		return owlConfig.getFreshEntityPolicy();
	}

	@Override
	public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return owlConfig.getIndividualNodeSetPolicy();
	}

	@Override
	public ReasonerProgressMonitor getProgressMonitor() {
		return owlConfig.getProgressMonitor();
	}

	@Override
	public long getTimeOut() {
		return owlConfig.getTimeOut();
	}

	public ReasonerConfiguration getElkConfiguration() {
		return elkConfig;
	}
}
