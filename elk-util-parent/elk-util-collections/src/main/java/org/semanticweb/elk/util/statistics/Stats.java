package org.semanticweb.elk.util.statistics;

/*-
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2024 Department of Computer Science, University of Oxford
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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Stats {

	public static final String STAT_NAME_SEPARATOR = ".";

	private Stats() {
		// Forbid instantiation of a utility class.
	}

	public static Map<String, Object> copyIntoMap(final Object hasStats,
			Map<String, Object> result) {
		if (result == null) {
			result = new HashMap<String, Object>();
		}
		final Iterable<Map.Entry<String, Object>> stats = getStats(hasStats);
		for (final Map.Entry<String, Object> e : stats) {
			result.put(e.getKey(), e.getValue());
		}
		return result;
	}

	public static Map<String, Object> copyIntoMap(final Object hasStats) {
		return copyIntoMap(hasStats, new HashMap<String, Object>());
	}

	public static Iterable<Map.Entry<String, Object>> getStats(
			final Object hasStats) {
		return getStats(hasStats, "");
	}

	public static Iterable<Map.Entry<String, Object>> getStats(
			final Object hasStats, final String statNamePrefix) {
		Preconditions.checkNotNull(hasStats);
		Preconditions.checkNotNull(statNamePrefix);
		if (hasStats instanceof Class) {
			return getStats((Class<?>) hasStats, null, statNamePrefix);
		} else {
			return getStats(hasStats.getClass(), hasStats, statNamePrefix);
		}
	}

	public static Iterable<Map.Entry<String, Object>> getStats(
			final Class<?> hasStatsClass, final Object hasStats,
			final String statNamePrefix) {
		Preconditions.checkNotNull(hasStatsClass);
		Preconditions.checkNotNull(statNamePrefix);

		final Iterable<Field> statFields = getAnnotatedElements(Stat.class,
				hasStatsClass.getFields());
		final Iterable<Map.Entry<String, Object>> fieldStats = Iterables
				.transform(statFields,
						new Function<Field, Map.Entry<String, Object>>() {

							@Override
							public Map.Entry<String, Object> apply(
									final Field field) {
								return new AbstractMap.SimpleImmutableEntry<String, Object>(
										statNamePrefix + getStatName(field),
										checkedGet(field, hasStats));
							}

						});

		final Iterable<Method> statMethods = getAnnotatedElements(Stat.class,
				hasStatsClass.getMethods());
		final Iterable<Map.Entry<String, Object>> methodStats = Iterables
				.transform(statMethods,
						new Function<Method, Map.Entry<String, Object>>() {

							@Override
							public Map.Entry<String, Object> apply(
									final Method method) {
								return new AbstractMap.SimpleImmutableEntry<String, Object>(
										statNamePrefix + getStatName(method),
										checkedInvoke(method, hasStats));
							}

						});

		// Recursion into nested stats

		final Iterable<Map.Entry<String, Object>> nesteds = getNested(
				hasStatsClass, hasStats, statNamePrefix);
		final Iterable<Map.Entry<String, Object>> nestedStats = Iterables
				.concat(Iterables.transform(nesteds,
						new Function<Map.Entry<String, Object>, Iterable<Map.Entry<String, Object>>>() {

							@Override
							public Iterable<Map.Entry<String, Object>> apply(
									final Map.Entry<String, Object> entry) {
								return getStats(entry.getValue(),
										entry.getKey() + STAT_NAME_SEPARATOR);
							}

						}));

		return Iterables.concat(fieldStats, methodStats, nestedStats);
	}

	public static void resetStats(final Object hasStats) {
		Preconditions.checkNotNull(hasStats);
		if (hasStats instanceof Class) {
			resetStats((Class<?>) hasStats, null);
		} else {
			resetStats(hasStats.getClass(), hasStats);
		}
	}

	public static void resetStats(final Class<?> hasStatsClass,
			final Object hasStats) {
		Preconditions.checkNotNull(hasStatsClass);

		final Iterable<Method> resetMethods = getAnnotatedElements(
				ResetStats.class, hasStatsClass.getMethods());
		for (final Method resetMethod : resetMethods) {
			checkedInvoke(resetMethod, hasStats);
		}

		// Recursion into nested stats

		final Iterable<Map.Entry<String, Object>> nesteds = getNested(
				hasStatsClass, hasStats);
		for (final Map.Entry<String, Object> entry : nesteds) {
			resetStats(entry.getValue());
		}

	}

	private static Iterable<Map.Entry<String, Object>> getNested(
			final Class<?> hasStatsClass, final Object hasStats) {
		return getNested(hasStatsClass, hasStats, "");
	}

	private static Iterable<Map.Entry<String, Object>> getNested(
			final Class<?> hasStatsClass, final Object hasStats,
			final String statNamePrefix) {
		Preconditions.checkNotNull(hasStatsClass);
		Preconditions.checkNotNull(statNamePrefix);

		final Iterable<Field> nestedFields = getAnnotatedElements(
				NestedStats.class, hasStatsClass.getFields());
		final Iterable<Map.Entry<String, Object>> fieldNesteds = Iterables
				.transform(nestedFields,
						new Function<Field, Map.Entry<String, Object>>() {

							@Override
							public Map.Entry<String, Object> apply(
									final Field field) {
								return new AbstractMap.SimpleImmutableEntry<String, Object>(
										statNamePrefix
												+ getNestedStatsName(field),
										checkedGet(field, hasStats));
							}

						});

		final Iterable<Method> nestedMethods = getAnnotatedElements(
				NestedStats.class, hasStatsClass.getMethods());
		final Iterable<Map.Entry<String, Object>> methodNesteds = Iterables
				.transform(nestedMethods,
						new Function<Method, Map.Entry<String, Object>>() {

							@Override
							public Map.Entry<String, Object> apply(
									final Method method) {
								return new AbstractMap.SimpleImmutableEntry<String, Object>(
										statNamePrefix
												+ getNestedStatsName(method),
										checkedInvoke(method, hasStats));
							}

						});

		return Iterables.concat(fieldNesteds, methodNesteds);
	}

	private static Object checkedGet(final Field field, final Object object) {
		if (object == null && !Modifier.isStatic(field.getModifiers())) {
			throw new StatsException(
					"Can handle only static fields! Non-static field: "
							+ field);
		}
		try {
			return field.get(object);
		} catch (final IllegalAccessException e) {
			throw new StatsException(e);
		}
	}

	private static Object checkedInvoke(final Method method,
			final Object object) {
		if (object == null && !Modifier.isStatic(method.getModifiers())) {
			throw new StatsException(
					"Can handle only static methods! Non-static method: "
							+ method);
		}
		if (method.getParameterTypes().length != 0) {
			throw new StatsException(
					"Can handle only methods with no parameters! Method with parameters: "
							+ method);
		}
		try {
			return method.invoke(object);
		} catch (final IllegalAccessException e) {
			throw new StatsException(e);
		} catch (final InvocationTargetException e) {
			throw new StatsException(e);
		}
	}

	private static <E extends AnnotatedElement> Iterable<E> getAnnotatedElements(
			final Class<? extends Annotation> presentAnnotation,
			final E[] elements) {
		return Iterables.filter(Arrays.asList(elements), new Predicate<E>() {

			@Override
			public boolean apply(final E element) {
				return element.isAnnotationPresent(presentAnnotation);
			}

		});
	}

	private static <E extends AnnotatedElement & Member> String getStatName(
			final E element) {
		final String name = element.getAnnotation(Stat.class).name();
		return name.isEmpty() ? element.getName() : name;
	}

	private static <E extends AnnotatedElement & Member> String getNestedStatsName(
			final E element) {
		final String name = element.getAnnotation(NestedStats.class).name();
		return name.isEmpty() ? element.getName() : name;
	}

}
