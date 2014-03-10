/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.alc.indexing.entries;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.util.collections.entryset.KeyEntry;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;

/**
 * A visitor for {@link IndexedClassExpression}s and
 * {@link IndexedPropertyChain}s that wraps the visited objects in the
 * corresponding Entry wrapper to redefine equality.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the elements for which the wrapped entries can be used
 * @see KeyEntryHashSet
 */
public class IndexedEntryConverter<T> implements
		IndexedObjectVisitor<KeyEntry<T, ? extends IndexedObject>> {

	@Override
	public IndexedClassExpressionEntry<T, IndexedClass> visit(
			IndexedClass element) {
		return new IndexedClassEntry<T, IndexedClass>(element);
	}

	@Override
	public IndexedClassExpressionEntry<T, IndexedObjectIntersectionOf> visit(
			IndexedObjectIntersectionOf element) {
		return new IndexedObjectIntersectionOfEntry<T, IndexedObjectIntersectionOf>(
				element);
	}

	@Override
	public KeyEntry<T, ? extends IndexedObjectUnionOf> visit(
			IndexedObjectUnionOf element) {
		return new IndexedObjectUnionOfEntry<T, IndexedObjectUnionOf>(element);
	}

	@Override
	public IndexedClassExpressionEntry<T, IndexedObjectSomeValuesFrom> visit(
			IndexedObjectSomeValuesFrom element) {
		return new IndexedObjectSomeValuesFromEntry<T, IndexedObjectSomeValuesFrom>(
				element);
	}

	@Override
	public IndexedObjectPropertyEntry<T, IndexedObjectProperty> visit(
			IndexedObjectProperty element) {
		return new IndexedObjectPropertyEntry<T, IndexedObjectProperty>(element);
	}

	@Override
	public KeyEntry<T, ? extends IndexedSubClassOfAxiom> visit(
			IndexedSubClassOfAxiom axiom) {
		return new IndexedSubClassOfAxiomEntry<T, IndexedSubClassOfAxiom>(axiom);
	}

}
