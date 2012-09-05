/*
 * #%L
 * elk-reasoner
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

package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.stages.ObjectPropertyCompositionsPrecomputationStage;
import org.semanticweb.elk.reasoner.stages.ObjectPropertyHierarchyComputationStage;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * 
 * This object is used for fast retrieval of property inclusions and
 * compositions which are needed during saturation of class expressions.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturatedPropertyChain {
	final IndexedPropertyChain root;

	final Set<IndexedPropertyChain> derivedSubProperties;
	final Set<IndexedPropertyChain> derivedSuperProperties;

	final Set<IndexedBinaryPropertyChain> derivedSubCompositions;
	final Set<IndexedPropertyChain> derivedRightSubProperties;
	final Set<IndexedPropertyChain> leftComposableProperties;

	boolean isReflexive = false;
	boolean hasReflexiveRightSubProperty = false;
	boolean hasReflexiveLeftComposableProperty = false;

	AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeftSubProperty;
	AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRightSubProperty;

	public SaturatedPropertyChain(IndexedPropertyChain iop) {
		this.root = iop;
		this.derivedSuperProperties = new ArrayHashSet<IndexedPropertyChain>();
		this.derivedSubProperties = new ArrayHashSet<IndexedPropertyChain>();
		this.derivedSubCompositions = new ArrayHashSet<IndexedBinaryPropertyChain>();
		this.derivedRightSubProperties = new ArrayHashSet<IndexedPropertyChain>();
		this.leftComposableProperties = new ArrayHashSet<IndexedPropertyChain>();
	}

	public IndexedPropertyChain getRoot() {
		return root;
	}

	/**
	 * @return All sub-properties R of root including root itself. Computed in
	 *         the {@link ObjectPropertyHierarchyComputationStage}.
	 */
	public Set<IndexedPropertyChain> getSubProperties() {
		return derivedSubProperties;
	}

	/**
	 * @return All sub-properties R of root that are instances of
	 *         {@link IndexedBinaryPropertyChain}. Computed in the
	 *         {@link ObjectPropertyHierarchyComputationStage}.
	 */
	public Set<IndexedBinaryPropertyChain> getSubCompositions() {
		return derivedSubCompositions;
	}

	/**
	 * @return {@code true} if this property has a reflexive sub-property.
	 */
	public boolean isReflexive() {
		return isReflexive;
	}

	/**
	 * @return All properties R such that there exists properties S1,...,Sn
	 *         (n>=0) for which S1 o ... o Sn o R => root follows from the role
	 *         inclusion axioms.
	 */
	public Set<IndexedPropertyChain> getRightSubProperties() {
		return derivedRightSubProperties;
	}

	/**
	 * @return {@code true} if there exists a reflexive property R and
	 *         properties S1,...,Sn (n>=0) for which S1 o ... o Sn o R => root
	 *         follows from the role inclusion axioms.
	 */
	public boolean hasReflexiveRightSubProperty() {
		return hasReflexiveRightSubProperty;
	}

	/**
	 * @return All properties R such that there exist properties S1,...,Sn
	 *         (n>=0) and T for which S1 o ... o Sn o R o root => T follows from
	 *         the role inclusion axioms.
	 */
	public Set<IndexedPropertyChain> getLeftComposableProperties() {
		return leftComposableProperties;
	}

	/**
	 * @return {@code true} if there exists a reflexive property R such that
	 *         there exist properties S1,...,Sn (n>=0) and T for which S1 o ...
	 *         o Sn o R o root => T follows from the role inclusion axioms.
	 */
	public boolean hasReflexiveLeftComposableProperty() {
		return hasReflexiveLeftComposableProperty;
	}

	/**
	 * @return All super-properties of the root property including root itself.
	 *         Computed in the {@link ObjectPropertyHierarchyComputationStage}.
	 */
	public Set<IndexedPropertyChain> getSuperProperties() {
		return derivedSuperProperties;
	}

	/**
	 * @return A multimap from R to S such that ObjectPropertyChain(R, root)
	 *         implies S, null if empty. Computed in the
	 *         {@link ObjectPropertyCompositionsPrecomputationStage} which
	 *         already expands premises of complex property inclusions under
	 *         property hierarchies
	 */
	public Multimap<IndexedPropertyChain, IndexedPropertyChain> getCompositionsByLeftSubProperty() {
		return compositionsByLeftSubProperty;
	}

	/**
	 * @return A multimap from R to S such that ObjectPropertyChain(root, R)
	 *         implies S, null if empty. Computed in the
	 *         {@link ObjectPropertyCompositionsPrecomputationStage} which
	 *         already expands premises of complex property inclusions under
	 *         property hierarchies
	 */
	public Multimap<IndexedPropertyChain, IndexedPropertyChain> getCompositionsByRightSubProperty() {
		return compositionsByRightSubProperty;
	}

}
