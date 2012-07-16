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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.semanticweb.elk.io.IOUtils;

//TODO: Documentation
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
	 * @return the {@link BaseConfiguration} for the specified parameters
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("static-method")
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

	@SuppressWarnings("static-method")
	public BaseConfiguration getConfiguration(InputStream source,
			String prefix, Class<? extends BaseConfiguration> configClass)
			throws ConfigurationException, IOException {

		ResourceBundle bundle = new PropertyResourceBundle(source);
		BaseConfiguration config = instantiate(configClass);

		copyParameters(prefix, config, bundle);

		return config;
	}

	/**
	 * Not a thread-safe method. Shouldn't be invoked concurrently.
	 * 
	 * @param configOnDisk
	 * @param config
	 * @throws ConfigurationException
	 * @throws IOException
	 */
	public void saveConfiguration(File configOnDisk, BaseConfiguration config)
			throws ConfigurationException, IOException {
		/*
		 * Unfortunately, we can't directly write the config on disk because the
		 * parameters in it may be just a subset of those on disk. So we load it
		 * first (alternatively one may use a singleton, which I typically try
		 * to avoid). It should work reasonably well unless there're too many
		 * parameters (in which case we should think of a mini key-value store).
		 */
		InputStream stream = null;
		BaseConfiguration loadedConfig = null;
		Properties diskProps = new Properties();

		try {
			stream = new FileInputStream(configOnDisk);
			loadedConfig = getConfiguration(stream, "", config.getClass());
			// copy parameters
			copyParameters(loadedConfig, diskProps);
		} catch (Exception e) {
			LOGGER_.info("Overwriting configuration since it can't be loaded (perhaps doesn't exist?)");
		} finally {
			IOUtils.closeQuietly(stream);
		}

		copyParameters(config, diskProps);
		// now save it to the file
		saveProperties(diskProps, configOnDisk);
	}

	private static void saveProperties(Properties diskProps, File configOnDisk) {
		OutputStream stream = null;

		try {
			stream = new FileOutputStream(configOnDisk);
			diskProps.store(
					stream,
					"ELK parameters saved at "
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
									.format(new Date()));
		} catch (FileNotFoundException e) {
			throw new ConfigurationException(
					"Configuration cannot be saved because the destination file cannot be written",
					e);
		} catch (IOException e) {
			throw new ConfigurationException(
					"Configuration cannot be saved because the destination file cannot be written",
					e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private static void copyParameters(BaseConfiguration config,
			Properties diskProps) {
		for (String key : config.getParameterNames()) {
			diskProps.setProperty(key, config.getParameter(key));
		}
	}

	private static void copyParameters(String prefix, BaseConfiguration config,
			ResourceBundle bundle) {
		for (String key : bundle.keySet()) {
			if (key.startsWith(prefix)) {
				config.setParameter(key, bundle.getString(key));
			}
		}
	}

	private static BaseConfiguration instantiate(
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