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
/**
 * 
 */
package org.semanticweb.elk.reasoner.config;

import org.semanticweb.elk.config.BaseConfiguration;
import org.semanticweb.elk.config.ConfigurationFactory;

/**
 * Configuration for the reasoner
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ReasonerConfiguration extends BaseConfiguration {

	// the only reason we don't use java.lang.Integer here is because the
	// default value is not a constant
	@Parameter(type = "org.semanticweb.elk.reasoner.config.NumberOfWorkers")
	public static final String NUM_OF_WORKING_THREADS = "elk.reasoner.number_of_workers";

	@Parameter(type = "org.semanticweb.elk.reasoner.config.UnsupportedFeatureTreatment", value = "IGNORE")
	public static final String UNSUPPORTED_FEATURE_TREATMENT = "elk.reasoner.unsupported_feature_treatment";
		
	@Parameter(type = "java.lang.Boolean", value = "true")
	public static final String INCREMENTAL_MODE_ALLOWED = "elk.reasoner.incremental.allowed";
	
	@Parameter(type = "java.lang.Boolean", value = "true")
	public static final String FLATTEN_INFERENCES = "elk.reasoner.flatten_inferences";
	
	public final static String REASONER_CONFIG_PREFIX = "elk.reasoner";

	public static ReasonerConfiguration getConfiguration() {
		return (ReasonerConfiguration) new ConfigurationFactory().getConfiguration(REASONER_CONFIG_PREFIX, ReasonerConfiguration.class);
	}
}