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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
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
	 * the composition of R1 and R2 derives only S1 and not S2. Note that this
	 * only makes sense if REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES is also on.
	 */
	public static final boolean ELIMINATE_IMPLIED_COMPOSITIONS = true;

	/**
	 * the {@code IndexedPropertyChain} for which this saturation is computed
	 */
	final IndexedPropertyChain root;

	/**
	 * {@code true} if {@link #root} is derived reflexive
	 */
	private volatile boolean isDerivedReflexive_ = false;

	/**
	 * the {@code IndexedPropertyChain}s which are subsumed by {@link #root}
	 */
	Set<IndexedPropertyChain> derivedSubProperties;

	/**
	 * A {@link Multimap} from R to S such that ObjectPropertyChain(R, root)
	 * implies S
	 */
	AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeftSubProperty;

	/**
	 * A {@link Multimap} from R to S such that ObjectPropertyChain(root, R)
	 * implies S
	 */
	AbstractHashMultimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRightSubProperty;

	public SaturatedPropertyChain(IndexedPropertyChain ipc) {
		this.root = ipc;
	}

	/**
	 * Clear all derived information for this {@link SaturatedPropertyChain}
	 */
	public void clear() {
		isDerivedReflexive_ = false;
		derivedSubProperties = null;
		compositionsByLeftSubProperty = null;
		compositionsByRightSubProperty = null;
	}

	/**
	 * @return {@code true} if there is no derived information in this
	 *         {@link SaturatedPropertyChain}, that is, its state is the same as
	 *         after applying {{@link #clear()}
	 */
	public boolean isClear() {
		return isDerivedReflexive_ == false && derivedSubProperties == null
				&& compositionsByLeftSubProperty == null
				&& compositionsByRightSubProperty == null;
	}

	@Override
	public SaturatedPropertyChain clone() {
		SaturatedPropertyChain result = new SaturatedPropertyChain(root);
		result.isDerivedReflexive_ = this.isDerivedReflexive_;
		result.derivedSubProperties = this.derivedSubProperties;
		result.compositionsByLeftSubProperty = this.compositionsByLeftSubProperty;
		result.compositionsByRightSubProperty = this.compositionsByRightSubProperty;
		return result;
	}

	/**
	 * @return All sub-properties R of root including root itself. Computed in
	 *         the {@link ObjectPropertyHierarchyComputationStage}.
	 */
	public Set<IndexedPropertyChain> getSubProperties() {
		return derivedSubProperties == null ? Collections
				.<IndexedPropertyChain> emptySet() : derivedSubProperties;
	}

	/**
	 * @return {@code true} if this property was derived to be reflexive.
	 */
	public boolean isDerivedReflexive() {
		return isDerivedReflexive_;
	}

	/**
	 * @return A {@link Multimap} from R to S such that ObjectPropertyChain(R,
	 *         root) implies S
	 */
	public Multimap<IndexedPropertyChain, IndexedPropertyChain> getCompositionsByLeftSubProperty() {
		return compositionsByLeftSubProperty == null ? Operations
				.<IndexedPropertyChain, IndexedPropertyChain> emptyMultimap()
				: compositionsByLeftSubProperty;
	}

	/**
	 * @return A {@link Multimap} from R to S such that
	 *         ObjectPropertyChain(root, R) implies S
	 */
	public Multimap<IndexedPropertyChain, IndexedPropertyChain> getCompositionsByRightSubProperty() {
		return compositionsByRightSubProperty == null ? Operations
				.<IndexedPropertyChain, IndexedPropertyChain> emptyMultimap()
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
	 * @param ipc
	 *            the {@link IndexedPropertyChain} for which to retrieve the
	 *            assigned {@link SaturatedPropertyChain}
	 * @return the {@link SaturatedPropertyChain} assigned to the given
	 *         {@link IndexedPropertyChain} creating new assignment if necessary
	 */
	public static SaturatedPropertyChain getCreate(IndexedPropertyChain ipc) {
		SaturatedPropertyChain saturated = ipc.getSaturated();
		if (saturated == null) {
			saturated = new SaturatedPropertyChain(ipc);
			SaturatedPropertyChain previous = ipc.setSaturated(saturated);
			if (previous != null)
				return previous;
		}
		return saturated;
	}

	/**
	 * Determines if the given {@link IndexedPropertyChain} can be produced in
	 * {@link BackwardLink}s or {@link ForwardLink}s.
	 * 
	 * @param ipc
	 * @return
	 */
	public static boolean isRelevant(IndexedPropertyChain ipc) {
		return !REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES
				|| ipc.accept(TOLD_SUPER_PROPERRTY_CHECKER_);
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
		Operations.dumpDiff(this.getSubProperties(), other.getSubProperties(),
				writer, root + ": this sub-property not in other: ");
		Operations.dumpDiff(other.getSubProperties(), this.getSubProperties(),
				writer, root + ": other sub-property not in this: ");
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

	/**
	 * an {@link IndexedPropertyChainVisitor} that determines if an
	 * {@link IndexedPropertyChain} can be a told super-property.
	 */
	private static final IndexedPropertyChainVisitor<Boolean> TOLD_SUPER_PROPERRTY_CHECKER_ = new IndexedPropertyChainVisitor<Boolean>() {

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			return true;
		}

		@Override
		public Boolean visit(IndexedBinaryPropertyChain element) {
			return !element.getRightChains().isEmpty();
		}

	};

}