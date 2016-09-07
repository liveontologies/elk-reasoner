/*
 * #%L
 * ELK Command Line Interface
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
package org.semanticweb.elk.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.collections.Operations;

public final class CliTestUtil {

	private CliTestUtil() {
		// Empty.
	}

	/**
	 * Converts a collection of {@link ElkEntity}-ies to an object that can be
	 * tested for semantic equality with other entity collection converted in
	 * this way by {@link Object#equals(Object)}.
	 * 
	 * (This does not generally work by calling {@link Object#equals(Object)}
	 * directly on the collection, because IRI-s of the entities may be
	 * abbreviated.)
	 * 
	 * @param entities
	 *            The entity collection.
	 * @param comparator
	 *            Comparator that compares entities semantically.
	 * @return
	 */
	public static <E extends ElkEntity> List<E> entities2Equalable(
			final Collection<E> entities,
			final Comparator<? super E> comparator) {

		final List<E> result = new ArrayList<E>(entities);
		Collections.sort(result, comparator);

		return result;
	}

	/**
	 * Converts a collection of {@link ElkEntity}-ies to an object that can be
	 * tested for semantic equality with other entity collection converted in
	 * this way by {@link Object#equals(Object)}.
	 * 
	 * (This does not generally work by calling {@link Object#equals(Object)}
	 * directly on the collection, because IRI-s of the entities may be
	 * abbreviated.)
	 * 
	 * @param entities
	 *            The entity collection.
	 * @param comparator
	 *            Comparator that compares entities semantically.
	 * @return
	 */
	public static <E extends ElkEntity> List<E> entities2Equalable(
			final Node<E> entities, final Comparator<? super E> comparator) {

		final List<E> result = new ArrayList<E>(entities.size());
		for (final E entity : entities) {
			result.add(entity);
		}
		Collections.sort(result, comparator);

		return result;
	}

	/**
	 * Converts a collection of collections of {@link ElkEntity}-ies to an
	 * object that can be tested for semantic equality with other entity
	 * collection collection converted in this way by
	 * {@link Object#equals(Object)}.
	 * 
	 * (This does not generally work by calling {@link Object#equals(Object)}
	 * directly on the collection, because IRI-s of the entities may be
	 * abbreviated.)
	 * 
	 * @param related
	 *            The entity collection collection.
	 * @param comparator
	 *            Comparator that compares entities semantically.
	 * @return
	 */
	public static <E extends ElkEntity> List<List<E>> related2Equalable(
			final Collection<? extends Collection<E>> related,
			final Comparator<? super E> comparator) {

		final List<List<E>> result = new ArrayList<List<E>>(related.size());
		for (final Collection<E> rel : related) {
			result.add(entities2Equalable(rel, comparator));
		}
		Collections.sort(result, Operations.<E>lexicalOrder(comparator));

		return result;
	}

	/**
	 * Converts a collection of collections of {@link ElkEntity}-ies to an
	 * object that can be tested for semantic equality with other entity
	 * collection collection converted in this way by
	 * {@link Object#equals(Object)}.
	 * 
	 * (This does not generally work by calling {@link Object#equals(Object)}
	 * directly on the collection, because IRI-s of the entities may be
	 * abbreviated.)
	 * 
	 * @param related
	 *            The entity collection collection.
	 * @param comparator
	 *            Comparator that compares entities semantically.
	 * @return
	 */
	public static <E extends ElkEntity> List<List<E>> relatedNodes2Equalable(
			final Collection<? extends Node<E>> related,
			final Comparator<? super E> comparator) {

		final List<List<E>> result = new ArrayList<List<E>>(related.size());
		for (final Node<E> rel : related) {
			result.add(entities2Equalable(rel, comparator));
		}
		Collections.sort(result, Operations.<E>lexicalOrder(comparator));

		return result;
	}

}
