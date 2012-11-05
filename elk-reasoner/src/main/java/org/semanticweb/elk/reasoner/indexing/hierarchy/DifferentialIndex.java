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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.saturation.conclusions.IndexChange;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * An object representing incremental changes in the index. The changes are
 * stored in two maps: additions and deletions. The map for additions assigns to
 * every indexed class expression for which some index entries have been added,
 * a dummy {@link IndexedClassExpressionChange} object, whose fields are exactly
 * the additions for the indexed class expressions. Likewise, the map for
 * deletions assigns to every indexed class expression for which some index
 * entries have been deleted, a dummy {@link IndexedClassExpressionChange}
 * object, whose fields are exactly the deletions for the indexed class
 * expressions.
 * 
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 * 
 */
public class DifferentialIndex {

	private static final Logger LOGGER_ = Logger.getLogger(DifferentialIndex.class);	
	
	final IndexedObjectCache objectCache_;
	
	/**
	 * The map representing entries to be added to the ontology index; it maps
	 * indexed class expression to dummy index class expression objects whose
	 * fields represent the added entries for these class expressions
	 */
	final Map<IndexedClassExpression, IndexChange> indexAdditions;

	/**
	 * The map representing entries to be removed from the ontology index; it
	 * maps indexed class expression to dummy index class expression objects
	 * whose fields represent the removed entries for these class expressions
	 */
	final Map<IndexedClassExpression, IndexChange> indexDeletions;

	/**
	 * The list of ELK classes to be added to the signature of the ontology; the
	 * new signature of the ontology will be obtained by {@link #addedClasses}
	 * and removing {@link #removedClasses}; these sets are not necessarily
	 * disjoint.
	 */
	final List<ElkClass> addedClasses;

	/**
	 * The list of ELK classes to be removed from the signature of the ontology;
	 * the new signature of the ontology will be obtained by
	 * {@link #addedClasses} and removing {@link #removedClasses}; these sets
	 * are not necessarily disjoint.
	 */
	final List<ElkClass> removedClasses;
	
	final List<ElkNamedIndividual> addedIndividuals;
	
	final List<ElkNamedIndividual> removedIndividuals;
	
	private final ElkAxiomIndexerVisitor axiomInserter_;
	
	private final ElkAxiomIndexerVisitor axiomDeleter_;	
	

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index additions for these class expressions
	 * 
	 */
	public Map<IndexedClassExpression, IndexChange> getIndexAdditions() {
		return this.indexAdditions;
	}

	/**
	 * @return the map from indexed class expressions to the corresponding
	 *         objects containing index deletions for these class expressions
	 */
	public Map<IndexedClassExpression, IndexChange> getIndexDeletions() {
		return this.indexDeletions;
	}

	/**
	 * Get the object assigned to the given indexed class expression for storing
	 * index additions, or assign a new one if no such object has been assigned.
	 * 
	 * @param target
	 *            the indexed class expressions for which to find the changes
	 *            additions object
	 * @return the object which contains all index additions for the given
	 *         indexed class expression
	 */
	public boolean registerAdditions(IndexedClassExpression target, ContextRules rules) {
		IndexChange result = indexAdditions.get(target);

		if (result == null) {
			result = new IndexChange(rules);
			indexAdditions.put(target, result);
			
			return true;
		}
		else {
			return rules.addTo(result);	
		}
	}

	/**
	 * Get the object assigned to the given indexed class expression for storing
	 * index deletions, or assign a new one if no such object has been assigned.
	 * 
	 * @param target
	 *            the indexed class expressions for which to find the changes
	 *            deletions object
	 * @return the object which contains all index deletions for the given
	 *         indexed class expression
	 */
	public boolean registerDeletions(IndexedClassExpression target, ContextRules rules) {
		IndexChange result = indexDeletions.get(target);

		if (result == null) {
			result = new IndexChange(rules);
			indexDeletions.put(target, result);
			
			return true;
		}
		else {
			return rules.addTo(result);	
		}
	}


	/**
	 * @return the list of ELK classes to be added to the signature of the
	 *         ontology
	 */
	public List<ElkClass> getAddedClasses() {
		return this.addedClasses;
	}

	/**
	 * @return the list of ELK classes to be removed from the signature of the
	 *         ontology
	 */
	public List<ElkClass> getRemovedClasses() {
		return this.removedClasses;
	}
	
	public List<ElkNamedIndividual> getAddedIndividuals() {
		return this.addedIndividuals;
	}
	
	public List<ElkNamedIndividual> getRemovedIndividuals() {
		return this.removedIndividuals;
	}

	
	public DifferentialIndex(IndexedObjectCache objectCache, IndexedClass owlNothing) {
		this.indexAdditions = new ArrayHashMap<IndexedClassExpression, IndexChange>(127);
		this.indexDeletions = new ArrayHashMap<IndexedClassExpression, IndexChange>(127);
		this.addedClasses = new ArrayList<ElkClass>(127);
		this.removedClasses = new ArrayList<ElkClass>(127);
		this.addedIndividuals = new ArrayList<ElkNamedIndividual>();
		this.removedIndividuals = new ArrayList<ElkNamedIndividual>();
		this.objectCache_ = objectCache;
		this.axiomInserter_ = new ElkAxiomIndexerVisitor(objectCache, owlNothing, new IncrementalIndexUpdater(this), true);
		this.axiomDeleter_ = new ElkAxiomIndexerVisitor(objectCache, owlNothing, new IncrementalIndexUpdater(this), false);		
	}

	/**
	 * Commits the changes to the indexed objects and clears all changes.
	 */
	public void commit() {	
		// commit deletions
		for (IndexedClassExpression target : indexDeletions.keySet()) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Committing index deletions for " + target);
			}
			
			indexDeletions.get(target).removeFrom(target.getChainCompositionRules());
		}
		
		indexDeletions.clear();
		
		for (IndexedClassExpression target : indexAdditions.keySet()) {
			indexAdditions.get(target).addTo(target.getChainCompositionRules());			
		}
		
		indexAdditions.clear();
	}

	public void clearSignatureChange() {
		addedClasses.clear();
		removedClasses.clear();
		addedIndividuals.clear();
		removedIndividuals.clear();
	}
	
	public boolean isEmpty() {
		return indexAdditions.isEmpty() && indexDeletions.isEmpty();
	}
	
	
	public ElkAxiomProcessor getAxiomInserter() {
		return axiomInserter_;
	}

	
	public ElkAxiomProcessor getAxiomDeleter() {
		return axiomDeleter_;
	}	
}