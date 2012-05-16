/*
 * #%L
 * elk-util-common
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
package org.semanticweb.elk.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * The base class responsible for loading configurations
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConfigurationFactory {

	static final String STANDARD_RESOURCE_NAME = "elk";

	final static Logger LOGGER_ = Logger.getLogger(ConfigurationFactory.class);

	/**
	 * 
	 * @param prefix
	 * @param configClass
	 * @return
	 */
	public BaseConfiguration getConfiguration(String prefix,
			Class<? extends BaseConfiguration> configClass)
			throws ConfigurationException {
		// try to find elk.properties. if doesn't exist, use the default
		// parameter values for the given class
		ResourceBundle bundle = null;
		BaseConfiguration config = instantiate(configClass);

		try {
			bundle = ResourceBundle.getBundle(STANDARD_RESOURCE_NAME,
					Locale.getDefault(), configClass.getClassLoader());
		} catch (MissingResourceException e) {
		}

		if (bundle == null) {
			LOGGER_.info("Loading default configuration parameters for "
					+ configClass);
		} else {
			// copy parameters from the bundle
			copyParameters(prefix, config, bundle);
		}

		return config;
	}

	public BaseConfiguration getConfiguration(InputStream source,
			String prefix, Class<? extends BaseConfiguration> configClass)
			throws ConfigurationException, IOException {

		ResourceBundle bundle = new PropertyResourceBundle(source);
		BaseConfiguration config = instantiate(configClass);

		copyParameters(prefix, config, bundle);

		return config;
	}

	private void copyParameters(String prefix, BaseConfiguration config,
			ResourceBundle bundle) {
		for (String key : bundle.keySet()) {
			if (key.startsWith(prefix)) {
				config.setParameter(key, bundle.getString(key));
			}
		}
	}

	private BaseConfiguration instantiate(
			Class<? extends BaseConfiguration> configClass)
			throws ConfigurationException {
		BaseConfiguration config = null;

		try {
			config = configClass.getConstructor().newInstance();

			config.initConfiguration();

			return config;

		} catch (Exception e) {
			throw new ConfigurationException(
					"Failed to instantiate the configuration class "
							+ configClass);
		}
	}
}