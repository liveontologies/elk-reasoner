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

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.AbstractHashMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.Operations;

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
	 * the {@code IndexedPropertyChain} for which this saturation is computed
	 */
	final IndexedPropertyChain root;

	/**
	 * {@code true} if {@link #root} is derived reflexive
	 */
	private volatile boolean isDerivedReflexive_ = false;

	/**
	 * the {@code IndexedObjectProperty}s which are subsumed by {@link #root}
	 */
	Set<IndexedObjectProperty> derivedSubProperties;

	/**
	 * the {@code IndexedPropertyChain}s which are subsumed by {@link #root}
	 */
	Set<IndexedPropertyChain> derivedSubProperyChains;

	/**
	 * {@code true} if {@link #derivedSubProperties} and
	 * {@link #derivedSubProperyChains} are not {@code null} and fully computed
	 */
	volatile boolean derivedSubPropertiesComputed = false;

	/**
	 * the {@code IndexedClassExpression}s that are ranges of the
	 * {@code IndexedObjectProperty}s that subsume {@link #root}
	 */
	Set<IndexedClassExpression> derivedRanges;

	/**
	 * {@code true} if {@link #derivedRanges} is not {@code null} and fully
	 * computed
	 */
	volatile boolean derivedRangesComputed = false;

	/**
	 * a multimap T -> {S} such that both S and ObjectPropertyChain(S, T) imply
	 * {@link #root}
	 */
	Multimap<IndexedObjectProperty, IndexedObjectProperty> leftSubComposableSubPropertiesByRightProperties;

	/**
	 * {@code true} if {@link #leftSubComposableSubPropertiesByRightProperties}
	 * is not {@code null} and was fully computed
	 */
	volatile boolean leftSubComposableSubPropertiesByRightPropertiesComputed = false;

	/**
	 * A {@link Multimap} from R to S such that ObjectPropertyChain(R, root) is
	 * a subrole of S
	 */
	AbstractHashMultimap<IndexedObjectProperty, IndexedComplexPropertyChain> compositionsByLeftSubProperty;

	/**
	 * A {@link Multimap} from R to S such that ObjectPropertyChain(root, R) is
	 * a subrole of S
	 */
	AbstractHashMultimap<IndexedPropertyChain, IndexedComplexPropertyChain> compositionsByRightSubProperty;

	public SaturatedPropertyChain(IndexedPropertyChain ipc) {
		this.root = ipc;
	}

	/**
	 * Clear all derived information for this {@link SaturatedPropertyChain}
	 */
	public void clear() {
		isDerivedReflexive_ = false;
		derivedSubProperties = null;
		derivedSubProperyChains = null;
		derivedSubPropertiesComputed = false;
		leftSubComposableSubPropertiesByRightProperties = null;
		leftSubComposableSubPropertiesByRightPropertiesComputed = false;
		compositionsByLeftSubProperty = null;
		compositionsByRightSubProperty = null;
	}

	/**
	 * @return {@code true} if there is no derived information in this
	 *         {@link SaturatedPropertyChain}, that is, its state is the same as
	 *         after applying {@link #clear()}
	 */
	public boolean isClear() {
		return isDerivedReflexive_ == false && derivedSubProperties == null
				&& derivedSubProperyChains == null
				&& leftSubComposableSubPropertiesByRightProperties == null
				&& compositionsByLeftSubProperty == null
				&& compositionsByRightSubProperty == null;
	}

	/**
	 * @return All sub-{@link IndexedObjectProperty} of root including root, if
	 *         it is an {@link IndexedObjectProperty} itself.
	 */
	public Set<IndexedObjectProperty> getSubProperties() {
		if (derivedSubProperties != null)
			return derivedSubProperties;
		// else
		if (root instanceof IndexedObjectProperty)
			return Collections
					.<IndexedObjectProperty> singleton((IndexedObjectProperty) root);
		// else
		return Collections.emptySet();
	}

	/**
	 * @return All sub-{@link IndexedPropertyChain} of root including root
	 *         itself.
	 */
	public Set<IndexedPropertyChain> getSubPropertyChains() {
		return derivedSubProperyChains == null ? Collections
				.<IndexedPropertyChain> singleton(root)
				: derivedSubProperyChains;
	}

	/**
	 * @return All ranges of super-{@link IndexedObjectProperty} of root.
	 */
	public Set<IndexedClassExpression> getRanges() {
		if (derivedRanges == null)
			return Collections.emptySet();
		// else
		return derivedRanges;
	}

	/**
	 * @return {@code true} if this property was derived to be reflexive.
	 */
	public boolean isDerivedReflexive() {
		return isDerivedReflexive_;
	}

	/**
	 * @return A {@link Multimap} from R to S such that ObjectPropertyChain(R,
	 *         root) is a subrole of S
	 */
	public Multimap<IndexedObjectProperty, IndexedComplexPropertyChain> getCompositionsByLeftSubProperty() {
		return compositionsByLeftSubProperty == null ? Operations
				.<IndexedObjectProperty, IndexedComplexPropertyChain> emptyMultimap()
				: compositionsByLeftSubProperty;
	}

	/**
	 * @return A {@link Multimap} from R to S such that
	 *         ObjectPropertyChain(root, R) is a subrole of S
	 */
	public Multimap<IndexedPropertyChain, IndexedComplexPropertyChain> getCompositionsByRightSubProperty() {
		return compositionsByRightSubProperty == null ? Operations
				.<IndexedPropertyChain, IndexedComplexPropertyChain> emptyMultimap()
				: compositionsByRightSubProperty;
	}

	/* Functions that modify the saturation */

	/**
	 * Sets this property as reflexive
	 * 
	 * @return {@code true} if the reflexivity status of the property has
	 *         changed
	 */
	synchronized boolean setReflexive() {
		if (isDerivedReflexive_)
			return false;
		isDerivedReflexive_ = true;
		return true;
	}

	/**
	 * Prints differences with other {@link SaturatedPropertyChain}
	 * 
	 * @param other
	 *            the {@link SaturatedPropertyChain} with which to compare this
	 *            {@link SaturatedPropertyChain}
	 * @throws IOException
	 */
	public void dumpDiff(SaturatedPropertyChain other, Writer writer)
			throws IOException {

		// comparing roots
		if (this.root != other.root)
			writer.append("this root: " + root + "; other root: " + other.root
					+ "\n");
		// comparing reflexivity flags
		if (this.isDerivedReflexive_ != other.isDerivedReflexive_)
			writer.append(root + ": this reflexive: "
					+ this.isDerivedReflexive_ + "; other relfexive: "
					+ other.isDerivedReflexive_ + "\n");
		// comparing derived sub-properties
		Operations.dumpDiff(this.getSubPropertyChains(),
				other.getSubPropertyChains(), writer, root
						+ ": this sub-property not in other: ");
		Operations.dumpDiff(other.getSubPropertyChains(),
				this.getSubPropertyChains(), writer, root
						+ ": other sub-property not in this: ");
		// comparing derived compositions
		Operations.dumpDiff(this.getCompositionsByLeftSubProperty(),
				other.getCompositionsByLeftSubProperty(), writer, root
						+ ": this left composition not in other: ");
		Operations.dumpDiff(other.getCompositionsByLeftSubProperty(),
				this.getCompositionsByLeftSubProperty(), writer, root
						+ ": other left composition not in this: ");
		Operations.dumpDiff(this.getCompositionsByRightSubProperty(),
				other.getCompositionsByRightSubProperty(), writer, root
						+ ": this right composition not in other: ");
		Operations.dumpDiff(other.getCompositionsByRightSubProperty(),
				this.getCompositionsByRightSubProperty(), writer, root
						+ ": other right composition not in this: ");
	}
}