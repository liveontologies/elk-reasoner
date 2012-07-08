/*
 * #%L
 * ELK Reasoner Protege Plug-in
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
package org.semanticweb.elk.protege;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.elk.config.ConfigurationFactory;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

/**
 * A utility for saving/restoring config options from ./Protege
 * 
 * @author Pavel Klinov
 *
 */
public class ElkProtegeConfigurationUtils {

	private static final String CONFIG_PATH = "/.Protege/org.semanticweb.elk/";
	private static final String CONFIG_FILENAME = "elk.properties";	
	
	/*
	 * Attempts to load the configuration from
	 * ~/.Protege/org.semanticweb.elk/elk.properties
	 */
	public static ReasonerConfiguration loadConfiguration() {
		String homeDir = System.getProperty("user.home");
		File configFile = new File(homeDir + CONFIG_PATH + CONFIG_FILENAME);
		InputStream stream = null;

		try {
			stream = new FileInputStream(configFile);

			return (ReasonerConfiguration) new ConfigurationFactory()
					.getConfiguration(stream,
							ReasonerConfiguration.REASONER_CONFIG_PREFIX,
							ReasonerConfiguration.class);
		}
		catch (IOException e) {
			//Resort to the default config
			//TODO log it?
			return ReasonerConfiguration.getConfiguration();
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	public static void saveConfiguration(ReasonerConfiguration elkConfig) throws Exception {
		if (elkConfig != null) {
			// save the config
			String homeDir = System.getProperty("user.home");
			File saveDir = new File(homeDir + CONFIG_PATH);

			if (!saveDir.exists()) {
				if (!saveDir.mkdirs()) {
					// TODO Log it?
					return;
				}
			}

			File configFile = new File(homeDir + CONFIG_PATH + CONFIG_FILENAME);

			new ConfigurationFactory().saveConfiguration(configFile, elkConfig);
		}
	}	
}
