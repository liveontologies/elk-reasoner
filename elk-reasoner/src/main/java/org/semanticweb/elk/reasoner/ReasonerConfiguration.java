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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A simple class to hold ELK configuration parameters
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReasonerConfiguration {
	
	@Parameter
	public static final ConfigurationParameter<Integer> NUM_OF_WORKING_THREADS = new ConfigurationParameter<Integer>(
										"elk.reasoner.number_of_workers",
										Runtime.getRuntime().availableProcessors());
	/**
	 * The default configuration
	 */
	private static final ReasonerConfiguration defaultConfig;
	
	private final Map<String, String> paramMap;
	
	static {		
		defaultConfig = new ReasonerConfiguration(getDefaultParameterMap());
	}
	
	private static Map<String, String> getDefaultParameterMap() {
		Map<String, String> defaultMap = new HashMap<String, String>();
		Field[] allFields = ReasonerConfiguration.class.getDeclaredFields();

		for (Field field : allFields) {
			if (field.isAnnotationPresent(Parameter.class)) {
				ConfigurationParameter<?> fieldValue = null;
				
				try {
					fieldValue = (ConfigurationParameter<?>)field.get(null);
				} catch (Exception e) {
					throw new ReasonerConfigurationException(e);
				}
				
				defaultMap.put(fieldValue.getName(), fieldValue.getValue().toString()); 
			}
		}

		return defaultMap;
	}
	
	
	private ReasonerConfiguration(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ReasonerConfiguration getDefaultConfiguration() {
		return defaultConfig;
	}

	/*
	 * 
	 * @param properties
	 * @return
	 */
	public static ReasonerConfiguration createConfiguration(ResourceBundle properties) {
		Map<String, String> propMap = new HashMap<String, String>();
		
		for (String key : properties.keySet()) {
			propMap.put(key,  properties.getString(key));
		}
		
		return new ReasonerConfiguration(propMap);
	}
	
	public String getParameter(String name) {
		return paramMap.get(name);
	}
	
	public int getParameterAsInt(String name) {
		return Integer.valueOf(paramMap.get(name));
	}
	
	public void setParameter(String name, String value) {
		paramMap.put(name, value);
	}
	
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(paramMap.keySet());
	}
	
	/*
	 * Static fields marked w/ this annotation are treated as configuration parameters
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface Parameter {}
}