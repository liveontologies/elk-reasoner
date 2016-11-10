package org.semanticweb.elk.matching.root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

abstract class AbstractIndexedContextRootMatch<V extends ElkObject>
		implements IndexedContextRootMatch {

	private final V value_;

	private final List<? extends ElkClassExpression> rangeMatches_;

	/**
	 * hash code, computed on demand
	 */
	private int hashCode_ = 0;

	AbstractIndexedContextRootMatch(V value,
			List<? extends ElkClassExpression> rangeMatches) {
		this.value_ = value;
		this.rangeMatches_ = rangeMatches;
	}

	AbstractIndexedContextRootMatch(V value) {
		this(value, Collections.<ElkClassExpression> emptyList());
	}

	public final V getValue() {
		return value_;
	}

	@Override
	public final List<? extends ElkClassExpression> getRangeMatches() {
		return rangeMatches_;
	}

	@Override
	public final List<? extends ElkClassExpression> getFillerMatches(
			ElkObject.Factory factory) {
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>(
				rangeMatches_.size() + 1);
		result.add(getMainFillerMatch(factory));
		result.addAll(getRangeMatches());
		return result;
	}

	@Override
	public final ElkClassExpression toElkExpression(ElkObject.Factory factory) {
		ElkClassExpression filler = getMainFillerMatch(factory);
		if (getRangeMatches().isEmpty()) {
			return filler;
		}
		// else
		return factory.getObjectIntersectionOf(getFillerMatches(factory));
	}

	@Override
	public final int hashCode() {
		if (hashCode_ == 0) {
			hashCode_ = IndexedContextRootMatchHash.hashCode(this);
		}
		// else
		return hashCode_;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		// else
		if (o instanceof IndexedContextRootMatch) {
			return hashCode() == o.hashCode()
					&& accept(new IndexedContextRootMatchEquality(
							(IndexedContextRootMatch) o));
		}
		// else
		return false;
	}

	@Override
	public final String toString() {
		return IndexedContextRootMatchPrinter.toString(this);
	}

	List<ElkClassExpression> extendRangeMatches(
			ElkClassExpression newRangeMatch) {
		List<ElkClassExpression> newRangeMatches = new ArrayList<ElkClassExpression>(
				rangeMatches_.size() + 1);
		for (ElkClassExpression previous : rangeMatches_) {
			if (newRangeMatch.equals(previous)) {
				// nothing changes
				return null;
			}
			newRangeMatches.add(previous);
		}
		newRangeMatches.add(newRangeMatch);
		return newRangeMatches;
	}

}
