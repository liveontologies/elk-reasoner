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
import java.util.Collections;
import java.util.Iterator;
import org.semanticweb.elk.reasoner.datatypes.numbers.AbstractInterval;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;

/**
 * TODO: documentation
 * 
 * @author Pospishnyi Oleksandr
 */
class IntervalTreeDatatypeIndex implements DatatypeIndex {

	/**
	 * TODO: documentation
	 */
	protected IntervalTree<AbstractInterval, IndexedDatatypeExpression> tree;


	@Override
	public void addDatatypeExpression(IndexedDatatypeExpression ide) {
		if (tree == null) {
			tree = new IntervalTree<AbstractInterval, IndexedDatatypeExpression>();
		}
		AbstractInterval interval = (AbstractInterval) ide.getValueSpace();
		tree.add(interval, ide);
	}

	@Override
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		AbstractInterval interval = (AbstractInterval) ide.getValueSpace();
		return tree.remove(interval, ide);
	}

	@Override
	public Collection<IndexedDatatypeExpression> getSubsumersFor(
			IndexedDatatypeExpression ide) {
		if (tree == null) {
			return new ArrayList<IndexedDatatypeExpression>(1);
		}
		AbstractInterval interval = (AbstractInterval) ide.getValueSpace();
		Collection<IndexedDatatypeExpression> ret = tree.searchIncludes(interval);
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
		for (IndexedDatatypeExpression ide : tree.values()) {
			index.addDatatypeExpression(ide);
		}
	}

}
