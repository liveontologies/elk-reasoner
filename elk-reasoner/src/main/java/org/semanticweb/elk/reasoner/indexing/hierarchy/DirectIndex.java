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
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ChainableContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * 
 * 
 */
public class DirectIndex implements ModifiableOntologyIndex {

	final IndexedClass indexedOwlThing, indexedOwlNothing;

	final IndexedObjectCache objectCache;
	private ChainableContextInitRule contextInitRules_;

	private final Set<IndexedObjectProperty> reflexiveObjectProperties_;

	public DirectIndex(IndexedObjectCache objectCache) {
		this.objectCache = objectCache;
		this.reflexiveObjectProperties_ = new ArrayHashSet<IndexedObjectProperty>(
				64);

		// the context root initialization rule is always registered
		RootContextInitializationRule.addRuleFor(this);

		// index predefined entities
		MainAxiomIndexerVisitor tmpAxiomInserter = new MainAxiomIndexerVisitor(
				this, true);
		// TODO: what to do if someone tries to delete them?
		this.indexedOwlThing = tmpAxiomInserter
				.indexClassDeclaration(PredefinedElkClass.OWL_THING);
		this.indexedOwlNothing = tmpAxiomInserter
				.indexClassDeclaration(PredefinedElkClass.OWL_NOTHING);
	}

	public DirectIndex() {
		this(new IndexedObjectCache());
	}

	/* read-only methods required by the interface */

	@Override
	public LinkedContextInitRule getContextInitRuleHead() {
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
	public boolean addClass(ElkClass newClass) {
		// we do not rack signature changes
		return true;
	}

	@Override
	public boolean removeClass(ElkClass oldClass) {
		// we do not rack signature changes
		return true;
	}

	@Override
	public boolean addNamedIndividual(ElkNamedIndividual newIndividual) {
		// we do not rack signature changes
		return true;
	}

	@Override
	public boolean removeNamedIndividual(ElkNamedIndividual oldIndividual) {
		// we do not rack signature changes
		return true;
	}

	@Override
	public boolean addContextInitRule(ChainableContextInitRule newRule) {
		return newRule.addTo(getContextInitRuleChain());
	}

	@Override
	public boolean removeContextInitRule(ChainableContextInitRule oldRule) {
		return oldRule.removeFrom(getContextInitRuleChain());
	}

	@Override
	public boolean add(IndexedClassExpression target, ChainableSubsumerRule rule) {
		return rule.addTo(target.getCompositionRuleChain());
	}

	@Override
	public boolean remove(IndexedClassExpression target,
			ChainableSubsumerRule rule) {
		return rule.removeFrom(target.getCompositionRuleChain());
	}

	@Override
	public boolean add(IndexedObject newObject) {
		return newObject.accept(objectCache.inserter);
	}

	@Override
	public boolean remove(IndexedObject oldObject) {
		return oldObject.accept(objectCache.deletor);
	}

	@Override
	public boolean addReflexiveProperty(IndexedObjectProperty property) {
		return reflexiveObjectProperties_.add(property);
	}

	@Override
	public boolean removeReflexiveProperty(IndexedObjectProperty property) {
		return reflexiveObjectProperties_.remove(property);
	}

	/* class-specific methods */

	/**
	 * @return a {@link Chain} view of context initialization rules assigned to
	 *         this {@link OntologyIndex}; it can be used for inserting new
	 *         rules or deleting existing ones
	 */
	public Chain<ChainableContextInitRule> getContextInitRuleChain() {
		return new AbstractChain<ChainableContextInitRule>() {

			@Override
			public ChainableContextInitRule next() {
				return contextInitRules_;
			}

			@Override
			public void setNext(ChainableContextInitRule tail) {
				contextInitRules_ = tail;
			}
		};
	}

}
