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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkEntity;
import org.semanticweb.elk.syntax.ElkEntityVisitor;
import org.semanticweb.elk.syntax.ElkNamedIndividual;
import org.semanticweb.elk.syntax.ElkObjectHasSelf;
import org.semanticweb.elk.syntax.ElkObjectHasValue;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.ElkObjectOneOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyChain;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.util.Pair;

/**
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 *
 */
public class SerialOntologyIndex extends OntologyIndexModifier {

	protected final Map<ElkClassExpression, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<ElkObjectPropertyExpression, IndexedObjectProperty> indexedObjectPropertyLookup;
	protected final Map<ElkObjectPropertyChain, IndexedPropertyChain> indexedPropertyChainLookup;
	protected final Map<Pair<IndexedClassExpression, IndexedClassExpression>, IndexedObjectIntersectionOf> indexedObjectIntersectionOfLookup;
	protected final List<ComplexRoleInclusion> complexRoleInclusionLookup;
	private int indexedClassCount;

	public SerialOntologyIndex() {
		// TODO possibly replace by ArrayHashMap when it supports removals
		indexedClassExpressionLookup = new HashMap<ElkClassExpression, IndexedClassExpression>(
				1024);
		indexedObjectPropertyLookup = new HashMap<ElkObjectPropertyExpression, IndexedObjectProperty>(
				128);
		indexedPropertyChainLookup = new HashMap<ElkObjectPropertyChain, IndexedPropertyChain> (16);
		indexedObjectIntersectionOfLookup = new HashMap<Pair<IndexedClassExpression, IndexedClassExpression>, IndexedObjectIntersectionOf>(
				1024);
		
		// TODO possibly replace by a more efficient multiset datastructure
		complexRoleInclusionLookup =  new ArrayList<ComplexRoleInclusion> ();
	}

	public IndexedClassExpression getIndexedClassExpression(
			ElkClassExpression classExpression) {
		return indexedClassExpressionLookup.get(classExpression);
	}

	public IndexedObjectProperty getIndexedObjectProperty(
			ElkObjectPropertyExpression objectPropertyExpression) {
		return indexedObjectPropertyLookup.get(objectPropertyExpression);
	}
	
	public IndexedPropertyChain getIndexedPropertyChain(
			ElkObjectPropertyChain objectPropertyChain) {
		return indexedPropertyChainLookup.get(objectPropertyChain);
	}


	private class IndexedEntityGetter implements
			ElkEntityVisitor<IndexedEntity> {

		// TODO implement without a cast

		public IndexedEntity visit(ElkClass elkClass) {
			return (IndexedEntity) getIndexedClassExpression(elkClass);
		}

		public IndexedEntity visit(ElkObjectProperty elkObjectProperty) {
			return (IndexedEntity) getIndexedObjectProperty(elkObjectProperty);
		}

		public IndexedEntity visit(ElkNamedIndividual elkNamedIndividual) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private IndexedEntityGetter indexedEntityGetter = new IndexedEntityGetter();

	public IndexedEntity getIndexedEntity(ElkEntity entity) {
		return entity.accept(indexedEntityGetter);
	}

	@Override
	protected IndexedEntity createIndexed(ElkEntity entity) {
		return entity.accept(indexedEntityCreator);
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
		if (ice.occurrenceNo == 0) {
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
			indexedObjectPropertyLookup.remove(iop.representative);
	}
	
	@Override
	protected void removeIfNoOccurrence(IndexedPropertyChain ipc) {
		if (ipc.occurrenceNo == 0)
			if (ipc.representative != null)
				indexedPropertyChainLookup.remove(ipc.representative);
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

	private class IndexedEntityCreator implements
			ElkEntityVisitor<IndexedEntity> {
		public IndexedClass visit(ElkClass elkClass) {
			IndexedClass result = new IndexedClass(elkClass);
			indexedClassExpressionLookup.put(elkClass, result);
			indexedClassCount++;
			return result;
		}

		public IndexedObjectProperty visit(ElkObjectProperty elkObjectProperty) {
			IndexedObjectProperty result = new IndexedObjectProperty(
					elkObjectProperty);
			indexedObjectPropertyLookup.put(elkObjectProperty, result);
			return result;
		}

		public IndexedEntity visit(ElkNamedIndividual elkNamedIndividual) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private final IndexedEntityCreator indexedEntityCreator = new IndexedEntityCreator();

	private class IndexedClassExpressionCreator implements
			ElkClassExpressionVisitor<IndexedClassExpression> {
		public IndexedClass visit(ElkClass elkClass) {
			return indexedEntityCreator.visit(elkClass);
		}

		public IndexedClassExpression visit(
				ElkObjectIntersectionOf elkObjectIntersectionOf) {
			ArrayList<IndexedClassExpression> conjuncts = new ArrayList<IndexedClassExpression>(
					elkObjectIntersectionOf.getClassExpressions().size());

			for (ElkClassExpression c : elkObjectIntersectionOf
					.getClassExpressions()) {
				IndexedClassExpression ice = getIndexedClassExpression(c);
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
			IndexedObjectProperty relation = getIndexedObjectProperty(elkObjectSomeValuesFrom
					.getObjectPropertyExpression());
			if (relation == null)
				relation = createIndexed(elkObjectSomeValuesFrom
						.getObjectPropertyExpression());

			IndexedClassExpression filler = getIndexedClassExpression(elkObjectSomeValuesFrom
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

		public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
			// TODO Auto-generated method stub
			return null;
		}

		public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
			// TODO Auto-generated method stub
			return null;
		}

		public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private final IndexedClassExpressionCreator indexedClassExpressionCreator = new IndexedClassExpressionCreator();

	private class IndexedObjectPropertyCreator implements
			ElkObjectPropertyExpressionVisitor<IndexedObjectProperty> {

		public IndexedObjectProperty visit(ElkObjectProperty elkObjectProperty) {
			return indexedEntityCreator.visit(elkObjectProperty);
		}

		public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
			throw new UnsupportedOperationException();
		}
	};

	private final IndexedObjectPropertyCreator indexedObjectPropertyCreator = new IndexedObjectPropertyCreator();


	@Override
	IndexedPropertyChain createIndexed(ElkObjectPropertyChain representative) {
		List<? extends ElkObjectPropertyExpression> chain = representative.getObjectPropertyExpressions();
		int n = chain.size();
		assert n >= 3;
		
		IndexedPropertyExpression last = getIndexedObjectProperty(chain.get(n-1));
		if (last == null)
			last = createIndexed(chain.get(n-1));
		
		for (int i = n-2; i >= 1; i--) {
			IndexedObjectProperty next = getIndexedObjectProperty(chain.get(i));
			if (next == null)
				next = createIndexed(chain.get(i));
			
			IndexedPropertyChain ipc = new IndexedPropertyChain(next, last, (i == 1) ? representative : null);
			addComplexRoleInclusion(new ComplexRoleInclusion(next, last, ipc));
			last = ipc;
		}
		
		indexedPropertyChainLookup.put(representative, (IndexedPropertyChain) last);
		return (IndexedPropertyChain) last;
	}

	@Override
	void addComplexRoleInclusion(ComplexRoleInclusion ria) {
		complexRoleInclusionLookup.add(ria);
	}

	@Override
	boolean removeComplexRoleInclusion(ComplexRoleInclusion ria) {
		return complexRoleInclusionLookup.remove(ria);
	}

	public Iterable<IndexedPropertyChain> getNamedIndexedPropertyChains() {
		return Collections.unmodifiableCollection(indexedPropertyChainLookup
				.values());
	}

	public Collection<ComplexRoleInclusion> getComplexRoleInclusions() {
		return Collections.unmodifiableCollection(complexRoleInclusionLookup);
	}

}
