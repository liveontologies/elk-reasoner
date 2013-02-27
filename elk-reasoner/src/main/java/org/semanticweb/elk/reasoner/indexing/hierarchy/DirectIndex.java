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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

public class DirectIndex implements ModifiableOntologyIndex {

	final IndexedClass indexedOwlThing, indexedOwlNothing;

	final IndexedObjectCache objectCache;

	private final ElkAxiomIndexerVisitor directAxiomInserter_,
			directAxiomDeleter_;

	private ChainableRule<Context> contextInitRules_ = null;

	private final Set<IndexedObjectProperty> reflexiveObjectProperties_;

	public DirectIndex(IndexedObjectCache objectCache) {
		this.objectCache = objectCache;

		this.directAxiomInserter_ = new ElkAxiomIndexerVisitor(this, true);
		this.directAxiomDeleter_ = new ElkAxiomIndexerVisitor(this, false);
		// index predefined entities
		// TODO: what to do if someone tries to delete them?
		this.indexedOwlThing = directAxiomInserter_
				.indexClassDeclaration(PredefinedElkClass.OWL_THING);
		this.indexedOwlNothing = directAxiomInserter_
				.indexClassDeclaration(PredefinedElkClass.OWL_NOTHING);

		this.reflexiveObjectProperties_ = new ArrayHashSet<IndexedObjectProperty>(
				64);
	}

	public DirectIndex() {
		this(new IndexedObjectCache());
	}

	/* read-only methods required by the interface */

	@Override
	public LinkRule<Context> getContextInitRuleHead() {
		return contextInitRules_;
	}

	@Override
	public Collection<IndexedClassExpression> getIndexedClassExpressions() {
		return objectCache.indexedClassExpressionLookup;
	}

	@Override
	public Collection<IndexedClass> getIndexedClasses() {
		return new AbstractCollection<IndexedClass>() {
			@Override
			public Iterator<IndexedClass> iterator() {
				return Operations.filter(getIndexedClassExpressions(),
						IndexedClass.class).iterator();
			}

			@Override
			public int size() {
				return objectCache.indexedClassCount;
			}
		};
	}

	@Override
	public Collection<IndexedIndividual> getIndexedIndividuals() {
		return new AbstractCollection<IndexedIndividual>() {

			@Override
			public Iterator<IndexedIndividual> iterator() {
				return Operations.filter(getIndexedClassExpressions(),
						IndexedIndividual.class).iterator();
			}

			@Override
			public int size() {
				return objectCache.indexedIndividualCount;
			}

		};
	}

	@Override
	public Collection<IndexedPropertyChain> getIndexedPropertyChains() {
		return objectCache.indexedPropertyChainLookup;
	}

	@Override
	public Collection<IndexedObjectProperty> getIndexedObjectProperties() {
		return new AbstractCollection<IndexedObjectProperty>() {

			@Override
			public Iterator<IndexedObjectProperty> iterator() {
				return Operations.filter(getIndexedPropertyChains(),
						IndexedObjectProperty.class).iterator();
			}

			@Override
			public int size() {
				return objectCache.indexedObjectPropertyCount;
			}
		};
	}

	@Override
	public Collection<IndexedObjectProperty> getReflexiveObjectProperties() {
		return Collections.unmodifiableCollection(reflexiveObjectProperties_);
	}

	@Override
	public IndexedClass getIndexedOwlThing() {
		return indexedOwlThing;
	}

	@Override
	public IndexedClass getIndexedOwlNothing() {
		return indexedOwlNothing;
	}

	/* read-write methods required by the interface */

	@Override
	public IndexedObjectCache getIndexedObjectCache() {
		return this.objectCache;
	}

	@Override
	public ElkAxiomProcessor getAxiomInserter() {
		return directAxiomInserter_;
	}

	@Override
	public ElkAxiomProcessor getAxiomDeleter() {
		return directAxiomDeleter_;
	}

	@Override
	public void addClass(ElkClass newClass) {
	}

	@Override
	public void removeClass(ElkClass oldClass) {
	}

	@Override
	public void addNamedIndividual(ElkNamedIndividual newIndividual) {
	}

	@Override
	public void removeNamedIndividual(ElkNamedIndividual newIndividual) {
	}

	@Override
	public void add(IndexedClassExpression target, ChainableRule<Context> rule) {
		rule.addTo(target.getCompositionRuleChain());
	}

	@Override
	public void remove(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		if (!rule.removeFrom(target.getCompositionRuleChain()))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove composition rule " + rule.getName()
							+ " for " + target);
	}

	@Override
	public void add(ChainableRule<Context> rule) {
		rule.addTo(getContextInitRuleChain());
	}

	@Override
	public void remove(ChainableRule<Context> rule) {
		if (!rule.removeFrom(getContextInitRuleChain()))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove context initialization rule "
							+ rule.getName());
	}

	@Override
	public void add(IndexedObject object) {
		object.accept(objectCache.inserter);
	}

	@Override
	public void remove(IndexedObject object) {
		if (!object.accept(objectCache.deletor))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove indexed object from the cache " + object);
		if (object instanceof IndexedClassExpression) {
			IndexedClassExpression ice = (IndexedClassExpression) object;
			Context context = ice.getContext();
			if (context != null)
				context.removeLinks();
		}
	}

	@Override
	public void addReflexiveProperty(IndexedObjectProperty property) {
		reflexiveObjectProperties_.add(property);
	}

	@Override
	public void removeReflexiveProperty(IndexedObjectProperty property) {
		if (!reflexiveObjectProperties_.remove(property))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove reflexivity of object property " + property);
	}

	/* other methods */

	/**
	 * @return a {@link Chain} view of context initialization rules assigned to
	 *         this {@link OntologyIndex}; it can be used for inserting new
	 *         rules or deleting existing ones
	 */
	public Chain<ChainableRule<Context>> getContextInitRuleChain() {
		return new AbstractChain<ChainableRule<Context>>() {

			@Override
			public ChainableRule<Context> next() {
				return contextInitRules_;
			}

			@Override
			public void setNext(ChainableRule<Context> tail) {
				contextInitRules_ = tail;
			}
		};
	}

}