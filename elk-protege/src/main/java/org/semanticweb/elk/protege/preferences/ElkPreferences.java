package org.semanticweb.elk.protege.preferences;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

public class ElkPreferences {

	/**
	 * @return the {@link ReasonerConfiguration} with the settings from this ELK
	 *         preferences
	 */
	public static ReasonerConfiguration getElkConfig() {
		ReasonerConfiguration elkConfig = ReasonerConfiguration
				.getConfiguration();
		ElkGeneralPreferences elkGeneralPrefs = new ElkGeneralPreferences()
				.load();
		elkConfig.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				String.valueOf(elkGeneralPrefs.numberOfWorkers));
		elkConfig.setParameter(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED,
				String.valueOf(elkGeneralPrefs.incrementalMode));
		ElkProofPreferences elkProofPrefs = new ElkProofPreferences().load();
		elkConfig.setParameter(ReasonerConfiguration.FLATTEN_INFERENCES,
				String.valueOf(elkProofPrefs.flattenInferences));
		return elkConfig;
	}

}
