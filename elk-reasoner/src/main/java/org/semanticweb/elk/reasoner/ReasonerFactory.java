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
package org.semanticweb.elk.reasoner;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * The main factory to instantiate {@link Reasoner}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReasonerFactory {

	final static Logger LOGGER_ = Logger.getLogger(ReasonerFactory.class);
	static final String PROPERTY_RESOURCE_NAME = "elk";
	
	/**
	 * Creates {@link Reasoner} with the configuration loaded from elk.properties (if exists in the classpath) or the default configuration
	 * 
	 * @return ELK reasoner
	 */
	public Reasoner createReasoner() {
		return createReasoner(loadReasonerConfiguration());
	}
	
	/**
	 *  Creates {@link Reasoner} with the provided configuration
	 * 
	 * @param config
	 * @return ELK reasoner
	 */
	public Reasoner createReasoner(ReasonerConfiguration config) {
		return new Reasoner(Executors.newCachedThreadPool(), config.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS.getName()));
	}
	
	protected ReasonerConfiguration loadReasonerConfiguration() {
		// see if there's a property file in the classpath
		ResourceBundle bundle = null;
		ReasonerConfiguration config = null;

		try {
			bundle = ResourceBundle.getBundle(PROPERTY_RESOURCE_NAME, Locale.getDefault(), ReasonerFactory.class.getClassLoader());
		} catch (MissingResourceException e) {
		}

		if (bundle == null) {
			LOGGER_.info("Instantiating ELK reasoner with default configuration parameters");
			config = ReasonerConfiguration.getDefaultConfiguration();
		} else {
			config = ReasonerConfiguration.createConfiguration(bundle);
		}
		
		return config;
	}
}