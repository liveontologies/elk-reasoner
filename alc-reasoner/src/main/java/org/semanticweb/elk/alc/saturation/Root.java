package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A key for the {@link Context}. Two {@link Root}s are equal if they contain
 * the same {@link IndexedClassExpression}s. For each {@link Root} there should
 * exist at most one {@link Context} with this {@link Root} modulo equality.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class Root {

	private final IndexedClassExpression positiveSubsumer_;

	private Set<IndexedClassExpression> negativeSubsumers_;

	private Context context_;

	public Root(IndexedClassExpression positiveSubsumer,
			IndexedClassExpression... negativeSubsumers) {
		this.positiveSubsumer_ = positiveSubsumer;
		if (negativeSubsumers.length > 0) {
			this.negativeSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
					negativeSubsumers.length);
			for (int i = 0; i < negativeSubsumers.length; i++) {
				negativeSubsumers_.add(negativeSubsumers[i]);
			}
		}
	}

	public Root(IndexedClassExpression positiveSubsumer,
			Collection<IndexedClassExpression> negativeSubsumers) {
		this.positiveSubsumer_ = positiveSubsumer;
		if (negativeSubsumers.size() > 0) {
			this.negativeSubsumers_ = new ArrayHashSet<IndexedClassExpression>(
					negativeSubsumers.size());
			for (IndexedClassExpression negativeSubsumer : negativeSubsumers)
				negativeSubsumers_.add(negativeSubsumer);
		}
	}

	/**
	 * @param root
	 * @param negativeSubsumer
	 * @return the {@link Root} obtained by adding the given negative subsumer
	 *         to the given {@link Root}
	 */
	public static Root addNegativeSubsumer(Root root,
			IndexedClassExpression negativeSubsumer) {
		Set<IndexedClassExpression> negativeSubsumers = root
				.getNegatitveSubsumers();
		if (negativeSubsumers.contains(negativeSubsumer))
			return root;
		// else
		Root newRoot = new Root(root.getPositiveSubsumer(), negativeSubsumers);
		newRoot.addNegativeSubsumer(negativeSubsumer);
		return newRoot;
	}

	/**
	 * @param root
	 * @param negativeSubsumer
	 * @return the {@link Root} obtained by removing the given negative subsumer
	 *         from the given {@link Root}
	 */
	public static Root removeNegativeSubsumer(Root root,
			IndexedClassExpression negativeSubsumer) {
		Set<IndexedClassExpression> negativeSubsumers = root
				.getNegatitveSubsumers();
		if (!negativeSubsumers.contains(negativeSubsumer))
			return root;
		// else
		Root newRoot = new Root(root.getPositiveSubsumer(), negativeSubsumers);
		newRoot.removeNegativeSubsumer(negativeSubsumer);
		return newRoot;
	}

	public IndexedClassExpression getPositiveSubsumer() {
		return this.positiveSubsumer_;
	}

	public Set<IndexedClassExpression> getNegatitveSubsumers() {
		if (negativeSubsumers_ == null)
			return Collections.emptySet();
		// else
		return negativeSubsumers_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(positiveSubsumer_,
				getNegatitveSubsumers());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Root))
			return false;
		// else
		Root otherRoot = (Root) o;
		return positiveSubsumer_.equals(otherRoot.positiveSubsumer_)
				&& getNegatitveSubsumers().equals(
						otherRoot.getNegatitveSubsumers());
	}

	@Override
	public String toString() {
		return positiveSubsumer_.toString()
				+ (negativeSubsumers_ == null ? "" : "~"
						+ getNegatitveSubsumers().toString());
	}

	boolean addNegativeSubsumer(IndexedClassExpression negativeSubsumer) {
		if (negativeSubsumers_ == null)
			negativeSubsumers_ = new ArrayHashSet<IndexedClassExpression>(4);
		return negativeSubsumers_.add(negativeSubsumer);
	}

	boolean removeNegativeSubsumer(IndexedClassExpression negativeSubsumer) {
		if (negativeSubsumers_ == null)
			return false;
		// else
		if (negativeSubsumers_.remove(negativeSubsumer)) {
			if (negativeSubsumers_.isEmpty())
				negativeSubsumers_ = null;
			return true;
		}
		// else
		return false;
	}

	void setContext(Context context) {
		this.context_ = context;
	}

	Context getContext() {
		return this.context_;
	}

}
