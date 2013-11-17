/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.reasoner.datatypes.util.NumberUtils;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;

/**
 * An implementation of {@link DatatypeIndex} which uses an interval tree for
 * indexing indexed datatype expressions.
 * 
 * TODO parameterize to use interval trees for non-numerical datatypes.
 * 
 * @author Pospishnyi Oleksandr
 * @author Pavel Klinov
 */
class IntervalTreeDatatypeIndex implements DatatypeIndex {

	private IntervalTree<Interval<Number>, IndexedDatatypeExpression, Number> intervalTree_;

	@Override
	public void addDatatypeExpression(IndexedDatatypeExpression ide) {
		if (intervalTree_ == null) {
			intervalTree_ = new IntervalTree<Interval<Number>, IndexedDatatypeExpression, Number>(NumberUtils.COMPARATOR);
		}
		// TODO make IDE generic to avoid this unchecked cast?
		intervalTree_.add((Interval) ide.getValueSpace(), ide);
	}

	@Override
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		return intervalTree_.remove((Interval) ide.getValueSpace(), ide);
	}

	@Override
	public Collection<IndexedDatatypeExpression> getSubsumersFor(
			IndexedDatatypeExpression ide) {

		if (intervalTree_ == null) {
			return new ArrayList<IndexedDatatypeExpression>(1);
		}

		Collection<IndexedDatatypeExpression> ret = intervalTree_
				.searchIncludes((Interval) ide.getValueSpace());
		// perform type filtering
		Iterator<IndexedDatatypeExpression> iter = ret.iterator();

		while (iter.hasNext()) {
			IndexedDatatypeExpression next = iter.next();
			if (!ide.getValueSpace().getDatatype()
					.isCompatibleWith(next.getValueSpace().getDatatype())) {
				iter.remove();
			}
		}
		return ret;
	}

	@Override
	public void appendTo(DatatypeIndex index) {
		for (IndexedDatatypeExpression ide : intervalTree_.values()) {
			index.addDatatypeExpression(ide);
		}
	}

}
