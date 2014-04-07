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
import java.util.Iterator;
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

	private final IndexedClassExpression positiveMember_;

	private Set<IndexedClassExpression> negativeMembers_;

	private Context context_;

	public Root(IndexedClassExpression positiveMember,
			IndexedClassExpression... negativeMembers) {
		this.positiveMember_ = positiveMember;
		if (negativeMembers.length > 0) {
			this.negativeMembers_ = new ArrayHashSet<IndexedClassExpression>(
					negativeMembers.length);
			for (int i = 0; i < negativeMembers.length; i++) {
				negativeMembers_.add(negativeMembers[i]);
			}
		}
	}

	public Root(IndexedClassExpression positiveMember,
			Collection<IndexedClassExpression> negativeMembers) {
		this.positiveMember_ = positiveMember;
		
		if (!negativeMembers.isEmpty()) {
			this.negativeMembers_ = new ArrayHashSet<IndexedClassExpression>(
					negativeMembers.size());
			for (IndexedClassExpression negativeMember : negativeMembers)
				negativeMembers_.add(negativeMember);
		}
	}
	
	public Root(IndexedClassExpression positiveMember,
			Iterable<IndexedClassExpression> negativeMembers) {
		this.positiveMember_ = positiveMember;
		
		Iterator<IndexedClassExpression> negativeIterator = negativeMembers.iterator();
		
		if (negativeIterator.hasNext()) {
			this.negativeMembers_ = new ArrayHashSet<IndexedClassExpression>(4);
			
			while (negativeIterator.hasNext()) {
				negativeMembers_.add(negativeIterator.next());
			}
		}
	}

	/**
	 * @param root
	 * @param negativeMember
	 * @return the {@link Root} obtained by adding the given negative member to
	 *         the given {@link Root}
	 */
	public static Root addNegativeMember(Root root,
			IndexedClassExpression negativeMember) {
		Set<IndexedClassExpression> negativeMembers = root
				.getNegativeMembers();
		if (negativeMembers.contains(negativeMember))
			return root;
		// else
		Root newRoot = new Root(root.getPositiveMember(), negativeMembers);
		newRoot.addNegativeMember(negativeMember);
		return newRoot;
	}

	/**
	 * @param root
	 * @param negativeMember
	 * @return the {@link Root} obtained by removing the given negative member
	 *         from the given {@link Root}
	 */
	public static Root removeNegativeMember(Root root,
			IndexedClassExpression negativeMember) {
		Set<IndexedClassExpression> negativeMembers = root
				.getNegativeMembers();
		if (!negativeMembers.contains(negativeMember))
			return root;
		// else
		Root newRoot = new Root(root.getPositiveMember(), negativeMembers);
		newRoot.removeNegativeMember(negativeMember);
		return newRoot;
	}

	public IndexedClassExpression getPositiveMember() {
		return this.positiveMember_;
	}

	public Set<IndexedClassExpression> getNegativeMembers() {
		if (negativeMembers_ == null)
			return Collections.emptySet();
		// else
		return negativeMembers_;
	}

	public int size() {
		return getNegativeMembers().size() + 1;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(positiveMember_,
				getNegativeMembers());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Root))
			return false;
		// else
		Root otherRoot = (Root) o;
		return positiveMember_.equals(otherRoot.positiveMember_)
				&& getNegativeMembers()
						.equals(otherRoot.getNegativeMembers());
	}

	@Override
	public String toString() {
		return positiveMember_.toString()
				+ (negativeMembers_ == null ? "" : "~"
						+ getNegativeMembers().toString());
	}

	boolean addNegativeMember(IndexedClassExpression negativeMember) {
		if (negativeMembers_ == null)
			negativeMembers_ = new ArrayHashSet<IndexedClassExpression>(4);
		return negativeMembers_.add(negativeMember);
	}

	boolean removeNegativeMember(IndexedClassExpression negativeMember) {
		if (negativeMembers_ == null)
			return false;
		// else
		if (negativeMembers_.remove(negativeMember)) {
			if (negativeMembers_.isEmpty())
				negativeMembers_ = null;
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
