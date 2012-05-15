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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * A simple class to hold ELK configuration parameters. Each parameter should be
 * a static field annotated with @Parameter. You must specify its type and,
 * optionally, the default value. The type must be an Enum or a Java class with
 * a constructor that takes a String argument. When loading the property from
 * file, the framework will call that constructor to check that it can parse the
 * String value (to prevent passing incorrect values).
 * 
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ReasonerConfiguration {

	@Parameter(type = "org.semanticweb.elk.reasoner.config.NumberOfWorkers")
	// the only reason we don't use java.lang.Integer here is because the
	// default value is not a constant
	public static final String NUM_OF_WORKING_THREADS = "elk.reasoner.number_of_workers";

	@Parameter(type = "org.semanticweb.elk.reasoner.config.UnsupportedFeatureTreatment", value = "IGNORE")
	public static final String UNSUPPORTED_FEATURE_TREATMENT = "elk.reasoner.unsupported_feature_treatment";

	/**
	 * The default configuration
	 */
	private static final ReasonerConfiguration defaultConfig;

	/**
	 * 
	 */
	private static final Map<String, Validator> validators;

	private final Map<String, String> paramMap;

	static {

		Map<String, String> defaultMap = new HashMap<String, String>();
		Field[] allFields = ReasonerConfiguration.class.getDeclaredFields();

		validators = new HashMap<String, Validator>();

		for (Field field : allFields) {
			if (field.isAnnotationPresent(Parameter.class)) {
				// create the parameter with its default value
				Parameter annotation = field.getAnnotation(Parameter.class);
				Class<?> paramClass = null;
				Validator validator = null;
				String paramValue = null;
				String paramName = null;

				try {
					paramClass = Class.forName(annotation.type());
					validator = new Validator(paramClass);
					paramName = field.get(null).toString();
					paramValue = validator.create(annotation.value())
							.toString();

				} catch (Exception e) {
					throw new ReasonerConfigurationException(
							"Perhaps incorrect declaration of the configuration parameter "
									+ field.getName(), e);
				}

				defaultMap.put(paramName, paramValue);
				// attach a validator to this parameter
				validators.put(paramName, validator);
			}
		}

		defaultConfig = new ReasonerConfiguration(
				Collections.unmodifiableMap(defaultMap));
	}

	protected ReasonerConfiguration(Map<String, String> paramMap) {
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
	 * 
	 * @return
	 */
	public static ReasonerConfiguration createConfiguration(
			ResourceBundle properties) {
		ReasonerConfiguration config = getDefaultConfiguration().clone();

		for (String key : properties.keySet()) {
			config.setParameter(key, properties.getString(key));
		}

		return config;
	}

	public ReasonerConfiguration clone() {
		return new ReasonerConfiguration(new HashMap<String, String>(
				this.paramMap));
	}

	public String getParameter(String name) {
		return paramMap.get(name);
	}

	public int getParameterAsInt(String name) {
		return Integer.valueOf(paramMap.get(name));
	}

	public void setParameter(String name, String value) {
		if (!validate(name, value))
			throw new ReasonerConfigurationException("Wrong value " + value
					+ " for parameter " + name);

		paramMap.put(name, value);
	}

	private boolean validate(String name, String value) {
		Validator validator = validators.get(name);

		try {
			if (validator != null)
				validator.create(value);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(paramMap.keySet());
	}

	/*
	 * Static fields marked w/ this annotation are treated as configuration
	 * parameters
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface Parameter {
		String type();

		String value() default "";
	}
}

/**
 * Validates the string-based representation of each parameter by trying to
 * instantiate it
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class Validator {

	final Class<?> clazz;
	final Constructor<?> constructor;
	final Method valueOf;

	Validator(Class<?> clazz) throws Exception {
		this.clazz = clazz;

		if (clazz.isEnum()) {
			constructor = null;
			valueOf = clazz.getMethod("valueOf",
					new Class<?>[] { String.class });
		} else {
			constructor = clazz.getConstructor(new Class<?>[] { String.class });
			this.valueOf = null;
		}
	}

	Object create(String value) throws Exception {
		assert constructor != null || valueOf != null;

		if (constructor != null) {
			return constructor.newInstance(value);
		} else {
			return valueOf.invoke(null, value);
		}
	}
}