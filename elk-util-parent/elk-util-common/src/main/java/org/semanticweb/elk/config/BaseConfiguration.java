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
package org.semanticweb.elk.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
 * @author Peter Skocovsky
 */
public class BaseConfiguration {

	private final Map<String, Validator> validators = new HashMap<String, Validator>();

	private final Map<String, Object> paramMap = new HashMap<String, Object>();

	public BaseConfiguration() {
	}

	protected BaseConfiguration(BaseConfiguration config) {
		this.paramMap.putAll(config.paramMap);
		this.validators.putAll(config.validators);
	}

	protected void initConfiguration() {
		Field[] allFields = getClass().getDeclaredFields();

		for (Field field : allFields) {
			if (field.isAnnotationPresent(Parameter.class)) {
				// create the parameter with its default value
				Parameter annotation = field.getAnnotation(Parameter.class);

				try {
					Class<?> paramClass = Class.forName(annotation.type());
					Validator validator = newValidator(paramClass);
					String paramName = field.get(null).toString();
					Object paramValue = validator.create(annotation.value());

					paramMap.put(paramName, paramValue);
					// attach a validator to this parameter
					validators.put(paramName, validator);

				} catch (Throwable e) {
					throw new ConfigurationException(
							"Perhaps incorrect declaration of the configuration parameter "
									+ field.getName(),
							e);
				}
			}
		}

	}

	public Object getParameter(final String name) {
		return paramMap.get(name);
	}

	public int getParameterAsInt(final String name) {
		return Integer.valueOf("" + paramMap.get(name));
	}

	public boolean getParameterAsBoolean(final String name) {
		return Boolean.valueOf("" + paramMap.get(name));
	}

	public BaseConfiguration setParameter(final String name,
			final String value) {
		final Validator validator = validators.get(name);
		final Object createdValue;
		if (validator == null) {
			createdValue = value;
		} else {
			try {
				createdValue = validator.create(value);
			} catch (final Exception e) {
				throw new ConfigurationException(
						"Wrong value " + value + " for parameter " + name, e);
			}
		}
		paramMap.put(name, createdValue);
		return this;
	}

	public BaseConfiguration setParameters(
			final Map<String, String> parameters) {
		for (final Map.Entry<String, String> e : parameters.entrySet()) {
			setParameter(e.getKey(), e.getValue());
		}
		return this;
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

	public static Method getValueOfMethod(final Class<?> clazz) {
		try {
			final Method method = clazz.getMethod("valueOf", String.class);
			final int mods = method.getModifiers();
			final Class<?>[] params = method.getParameterTypes();
			if (Modifier.isPublic(mods) && Modifier.isStatic(mods)
					&& params.length == 1 && params[0].equals(String.class)) {
				return method;
			}
		} catch (final NoSuchMethodException e) {
			// Return null at the end.
		}
		return null;
	}

	public static Constructor<?> getStringConstructorMethod(
			final Class<?> clazz) {
		try {
			final Constructor<?> constructor = clazz
					.getConstructor(String.class);
			final int mods = constructor.getModifiers();
			if (Modifier.isPublic(mods)) {
				return constructor;
			}
		} catch (final NoSuchMethodException e) {
			// Return null at the end.
		}
		return null;
	}

	private static Validator newValidator(final Class<?> clazz)
			throws IllegalArgumentException {
		final Method valueOf = getValueOfMethod(clazz);
		if (valueOf != null) {
			return new ValueOfValidator(valueOf);
		}
		final Constructor<?> constructor = getStringConstructorMethod(clazz);
		if (constructor != null) {
			return new ConstructorValidator(constructor);
		}
		throw new IllegalArgumentException(
				"The class does not have valueOf method nor a constructor that accepts a String.");
	}

	/**
	 * Validates the string-based representation of each parameter by trying to
	 * instantiate it
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author Peter Skocovsky
	 */
	private static interface Validator {

		Object create(String value) throws Exception;

	}

	private static class ValueOfValidator implements Validator {

		private final Method valueOf;

		public ValueOfValidator(final Method valueOf) {
			this.valueOf = valueOf;
		}

		@Override
		public Object create(final String value) throws Exception {
			return valueOf.invoke(null, value);
		}

	}

	private static class ConstructorValidator implements Validator {

		private final Constructor<?> constructor;

		public ConstructorValidator(final Constructor<?> constructor) {
			this.constructor = constructor;
		}

		@Override
		public Object create(final String value) throws Exception {
			return constructor.newInstance(value);
		}

	}

}
