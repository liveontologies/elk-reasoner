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
package org.semanticweb.elk.reasoner.reduction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * The result of the transitive reduction for a satisfiable
 * {@link IndexedClassExpression}; it contains information about its equivalent
 * classes and direct subsumers.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the root {@link IndexedClassExpression}s of the
 *            {@link TransitiveReductionJob}s for which this output is computed
 * 
 * @see TransitiveReductionJob
 */
public class TransitiveReductionOutputEquivalentDirect<R extends IndexedClassExpression>
		extends TransitiveReductionOutputEquivalent<R> {

	final Map<IndexedClass, TransitiveReductionOutputEquivalent<IndexedClass>> directSubsumers;

	/**
	 * the union of the subsumers of the current direct subsumers
	 */
	private Set<IndexedClass> allSubsumers;

	public TransitiveReductionOutputEquivalentDirect(R root) {
		super(root);
		directSubsumers = new HashMap<IndexedClass, TransitiveReductionOutputEquivalent<IndexedClass>>();

	}

	/**
	 * Returns the list of partial outputs of transitive reduction, containing
	 * equivalent classes of direct, i.e., transitively reduced, subsumers of
	 * the root.
	 * 
	 * @return the list consisting of partial output of transitive reduction for
	 *         direct subsumers of the root
	 */
	public Collection<TransitiveReductionOutputEquivalent<IndexedClass>> getDirectSubsumers() {
		return directSubsumers.values();
	}

	public void addDirectSubsumer(IndexedClass subsumer) {
		TransitiveReductionOutputEquivalent<IndexedClass> output = new TransitiveReductionOutputEquivalent<IndexedClass>(
				subsumer);
		output.equivalent.add(subsumer.getElkEntity());
		directSubsumers.put(subsumer, output);
	}

	public void addToAllSubsumers(IndexedClass subsumer) {
		if (allSubsumers == null) {
			allSubsumers = new HashSet<IndexedClass>();
		}

		allSubsumers.add(subsumer);
	}

	public Set<IndexedClass> getAllSubsumers() {
		return allSubsumers == null ? Collections.<IndexedClass> emptySet()
				: allSubsumers;
	}

	public void clearAllSubsumers() {
		allSubsumers = null;
	}

	public void removeDirectSubsumer(IndexedClass subsumer) {
		directSubsumers.remove(subsumer);
	}

	public TransitiveReductionOutputEquivalent<IndexedClass> getTransitiveReductionOutputForDirectSubsumer(
			IndexedClass subsumer) {
		return directSubsumers.get(subsumer);
	}

	@Override
	public void accept(TransitiveReductionOutputVisitor<R> visitor) {
		visitor.visit(this);
	}

}
