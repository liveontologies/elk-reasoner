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
import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
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
	
	/**
	 * If set to true, then binary property chains that do not occur on the
	 * right of another chain are skipped in the derivation and replaced
	 * directly by all their told super-properties. For example, given an
	 * inclusion SubObjectPropertyOf(ObjectPropertyChain(R1 R2) R), the
	 * composition of R1 and R2 derives directly R skipping the auxiliary binary
	 * chain representing [R1 R2].
	 */
	public static final boolean REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES = true;
	
	/**
	 * If set to true, then compositions between each pair of R1 and R2 are
	 * reduced under role hierarchies. For example, given
	 * 
	 * SubObjectPropertyOf(ObjectPropertyChain(R1 R2) S1),
	 * SubObjectPropertyOf(ObjectPropertyChain(R1 R2) S2), and
	 * SubObjectPropertyOf(S1 S2),
	 * 
	 * the composition of R1 and R2 derives only S1 and not S2. Note that
	 * this only makes sense if REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES is
	 * also on.
	 */
	public static final boolean ELIMINATE_IMPLIED_COMPOSITIONS = true;
	
	
	enum REFLEXIVITY {TRUE, FALSE, UNKNOWN};
	
	final IndexedPropertyChain root;

	final Set<IndexedPropertyChain> derivedSubProperties;
	final Set<IndexedPropertyChain> derivedSuperProperties;

	final Set<IndexedBinaryPropertyChain> derivedSubCompositions;
	final Set<IndexedPropertyChain> derivedRightSubProperties;
	final Set<IndexedPropertyChain> leftComposableProperties;
	/**
	 * the enum used to distinguish non-reflexive properties from those whose reflexivity isn't yet known
	 */
	AtomicReference<REFLEXIVITY> isReflexive = new AtomicReference<REFLEXIVITY>(REFLEXIVITY.UNKNOWN);
	AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeftSubProperty;
	AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRightSubProperty;
	
	boolean computed = false;

	public SaturatedPropertyChain(IndexedPropertyChain iop) {
		this.root = iop;
		this.derivedSuperProperties = new ArrayHashSet<IndexedPropertyChain>();
		this.derivedSubProperties = new ArrayHashSet<IndexedPropertyChain>();
		this.derivedSubCompositions = new ArrayHashSet<IndexedBinaryPropertyChain>();
		this.derivedRightSubProperties = new ArrayHashSet<IndexedPropertyChain>();
		this.leftComposableProperties = new ArrayHashSet<IndexedPropertyChain>();
	}
	
	public SaturatedPropertyChain(IndexedPropertyChain iop, boolean reflexive) {
		this(iop);
		this.isReflexive.set(reflexive ? REFLEXIVITY.TRUE : REFLEXIVITY.FALSE);
	}	
	
	public SaturatedPropertyChain(SaturatedPropertyChain saturated) {
		this(saturated.getRoot());
		
		isReflexive.set(saturated.isReflexive.get());
	}	

	public boolean isComputed() {
		return computed;
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
		return isReflexive.get() == REFLEXIVITY.TRUE;
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
	 * @return All properties R such that there exist properties S1,...,Sn
	 *         (n>=0) and T for which S1 o ... o Sn o R o root => T follows from
	 *         the role inclusion axioms.
	 */
	public Set<IndexedPropertyChain> getLeftComposableProperties() {
		return leftComposableProperties;
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
	 *         implies S, null if empty. 
	 */
	public Multimap<IndexedPropertyChain, IndexedPropertyChain> getCompositionsByLeftSubProperty() {
		return compositionsByLeftSubProperty;
	}

	/**
	 * @return A multimap from R to S such that ObjectPropertyChain(root, R)
	 *         implies S, null if empty. 
	 */
	public Multimap<IndexedPropertyChain, IndexedPropertyChain> getCompositionsByRightSubProperty() {
		return compositionsByRightSubProperty;
	}

	boolean reflexivityDetermined() {
		return isReflexive.get() != REFLEXIVITY.UNKNOWN;
	}
}