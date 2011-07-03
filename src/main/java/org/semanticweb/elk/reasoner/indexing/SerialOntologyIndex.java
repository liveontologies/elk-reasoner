/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.util.Pair;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class SerialOntologyIndex extends OntologyIndexModifier {

	protected final Map<ElkClassExpression, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<ElkObjectPropertyExpression, IndexedObjectProperty> indexedObjectPropertyLookup;
	private final Map<Pair<IndexedClassExpression, IndexedClassExpression>, IndexedObjectIntersectionOf> indexedObjectIntersectionOfLookup;
	private int indexedClassCount;

	public SerialOntologyIndex() {
		// TODO possibly replace by ArrayHashMap when it supports removals
		indexedClassExpressionLookup = new HashMap<ElkClassExpression, IndexedClassExpression>(
				1024);
		indexedObjectPropertyLookup = new HashMap<ElkObjectPropertyExpression, IndexedObjectProperty>(
				128);
		indexedObjectIntersectionOfLookup = new HashMap<Pair<IndexedClassExpression, IndexedClassExpression>, IndexedObjectIntersectionOf>(
				1024);
	}

	public IndexedClassExpression getIndexed(ElkClassExpression classExpression) {
		return indexedClassExpressionLookup.get(classExpression);
	}

	public IndexedObjectProperty getIndexed(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return indexedObjectPropertyLookup.get(objectPropertyExpression);
	}

	@Override
	protected IndexedClassExpression createIndexed(
			ElkClassExpression classExpression) {
		return classExpression.accept(indexedClassExpressionCreator);
	}

	@Override
	protected IndexedObjectProperty createIndexed(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return objectPropertyExpression.accept(indexedObjectPropertyCreator);
	}

	@Override
	protected void removeIfNoOccurrence(IndexedClassExpression ice) {
		if (ice.negativeOccurrenceNo == 0 && ice.positiveOccurrenceNo == 0) {
			for (ElkClassExpression c : ice.getRepresentantatives()) {
				indexedClassExpressionLookup.remove(c);
				if (c instanceof ElkClass)
					indexedClassCount--;
			}

			if (ice instanceof IndexedObjectIntersectionOf) {
				IndexedObjectIntersectionOf i = (IndexedObjectIntersectionOf) ice;
				indexedObjectIntersectionOfLookup
						.remove(new Pair<IndexedClassExpression, IndexedClassExpression>(
								i.getFirstConjunct(), i.getSecondConjunct()));
			}
		}
	}

	@Override
	protected void removeIfNoOccurrence(IndexedObjectProperty iop) {
		if (iop.occurrenceNo == 0)
			indexedObjectPropertyLookup.remove(iop.getElkObjectProperty());
	}

	public ElkAxiomProcessor getAxiomInserter() {
		return new AxiomIndexer(this, 1);
	}

	public ElkAxiomProcessor getAxiomDeleter() {
		return new AxiomIndexer(this, -1);
	}

	public Iterable<IndexedClass> getIndexedClasses() {
		return new Iterable<IndexedClass>() {

			public Iterator<IndexedClass> iterator() {
				return new Iterator<IndexedClass>() {
					Iterator<IndexedClassExpression> i = indexedClassExpressionLookup
							.values().iterator();
					IndexedClass next = findNext();

					public boolean hasNext() {
						return next != null;
					}

					public IndexedClass next() {
						if (next != null) {
							IndexedClass result = next;
							next = findNext();
							return result;
						}
						throw new NoSuchElementException();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}

					private IndexedClass findNext() {
						while (i.hasNext()) {
							IndexedClassExpression ice = i.next();
							if (ice instanceof IndexedClass)
								return (IndexedClass) ice;
						}
						return null;
					}

				};
			}
		};
	}

	public int getIndexedClassCount() {
		return indexedClassCount;
	}

	public Iterable<IndexedClassExpression> getIndexedClassExpressions() {
		return Collections.unmodifiableCollection(indexedClassExpressionLookup
				.values());
	}

	public Iterable<IndexedObjectProperty> getIndexedObjectProperties() {
		return Collections.unmodifiableCollection(indexedObjectPropertyLookup
				.values());
	}

	private final ElkClassExpressionVisitor<IndexedClassExpression> indexedClassExpressionCreator = new ElkClassExpressionVisitor<IndexedClassExpression>() {

		public IndexedClassExpression visit(ElkClass elkClass) {
			IndexedClass result = new IndexedClass(elkClass);
			indexedClassExpressionLookup.put(elkClass, result);
			indexedClassCount++;
			return result;
		}

		public IndexedClassExpression visit(
				ElkObjectIntersectionOf elkObjectIntersectionOf) {
			ArrayList<IndexedClassExpression> conjuncts = new ArrayList<IndexedClassExpression>(
					elkObjectIntersectionOf.getClassExpressions().size());

			for (ElkClassExpression c : elkObjectIntersectionOf
					.getClassExpressions()) {
				IndexedClassExpression ice = getIndexed(c);
				if (ice == null)
					ice = c.accept(this);
				conjuncts.add(ice);
			}

			IndexedClassExpression result = null;
			for (IndexedClassExpression ice : conjuncts) {
				if (result == null) {
					result = ice;
					continue;
				}

				// TODO comparison shouldn't be on hash code
				IndexedClassExpression firstConjunct, secondConjunct;
				if (result.hashCode() < ice.hashCode()) {
					firstConjunct = result;
					secondConjunct = ice;
				} else {
					firstConjunct = ice;
					secondConjunct = result;
				}

				Pair<IndexedClassExpression, IndexedClassExpression> p = new Pair<IndexedClassExpression, IndexedClassExpression>(
						firstConjunct, secondConjunct);
				result = indexedObjectIntersectionOfLookup.get(p);
				if (result == null) {
					result = new IndexedObjectIntersectionOf(firstConjunct,
							secondConjunct);
					indexedObjectIntersectionOfLookup.put(p,
							(IndexedObjectIntersectionOf) result);
				}
			}

			result.getRepresentantatives().add(elkObjectIntersectionOf);
			indexedClassExpressionLookup.put(elkObjectIntersectionOf, result);
			return result;
		}

		public IndexedClassExpression visit(
				ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
			IndexedObjectProperty relation = getIndexed(elkObjectSomeValuesFrom
					.getObjectPropertyExpression());
			if (relation == null)
				relation = createIndexed(elkObjectSomeValuesFrom
						.getObjectPropertyExpression());

			IndexedClassExpression filler = getIndexed(elkObjectSomeValuesFrom
					.getClassExpression());
			if (filler == null)
				filler = createIndexed(elkObjectSomeValuesFrom
						.getClassExpression());

			IndexedObjectSomeValuesFrom result = new IndexedObjectSomeValuesFrom(
					relation, filler);
			result.getRepresentantatives().add(elkObjectSomeValuesFrom);
			indexedClassExpressionLookup.put(elkObjectSomeValuesFrom, result);
			return result;
		}
	};

	private final ElkObjectPropertyExpressionVisitor<IndexedObjectProperty> indexedObjectPropertyCreator = new ElkObjectPropertyExpressionVisitor<IndexedObjectProperty>() {

		public IndexedObjectProperty visit(ElkObjectProperty elkObjectProperty) {
			IndexedObjectProperty result = new IndexedObjectProperty(
					elkObjectProperty);
			indexedObjectPropertyLookup.put(elkObjectProperty, result);
			return result;
		}

		public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
			throw new UnsupportedOperationException();
		}
	};

}
