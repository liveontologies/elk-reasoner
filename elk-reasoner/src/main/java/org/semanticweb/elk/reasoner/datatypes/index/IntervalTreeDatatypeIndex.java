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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.semanticweb.elk.reasoner.datatypes.numbers.AbstractInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.DateTimeIntervalValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.NumericIntervalValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.PatternValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.BinaryValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.DateTimeValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.LiteralValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.NumericValue;
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
	/**
	 * TODO: documentation
	 */
	protected IntervalSelector intervalSelector;

	public IntervalTreeDatatypeIndex() {
		intervalSelector = new IntervalSelector();
	}

	@Override
	public void addDatatypeExpression(IndexedDatatypeExpression ide) {
		if (tree == null) {
			tree = new IntervalTree<AbstractInterval, IndexedDatatypeExpression>();
		}
		AbstractInterval interval = ide.getValueSpace()
				.accept(intervalSelector);
		tree.add(interval, ide);
	}

	@Override
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		AbstractInterval interval = ide.getValueSpace()
				.accept(intervalSelector);
		return tree.remove(interval, ide);
	}

	@Override
	public Collection<IndexedDatatypeExpression> getSubsumersFor(
			IndexedDatatypeExpression ide) {
		if (tree == null) {
			return Collections.EMPTY_LIST;
		}
		AbstractInterval interval = ide.getValueSpace()
				.accept(intervalSelector);
		Collection<IndexedDatatypeExpression> ret = tree
				.searchIncludes(interval);
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

	private class IntervalSelector implements
			ValueSpaceVisitor<AbstractInterval> {

		@Override
		public AbstractInterval visit(EntireValueSpace valueSpace) {
			return null;
		}

		@Override
		public AbstractInterval visit(EmptyValueSpace valueSpace) {
			return null;
		}

		@Override
		public AbstractInterval visit(DateTimeIntervalValueSpace valueSpace) {
			return null;
		}

		@Override
		public AbstractInterval visit(LengthRestrictedValueSpace valueSpace) {
			return null;
		}

		@Override
		public AbstractInterval visit(NumericIntervalValueSpace valueSpace) {
			return valueSpace;
		}

		@Override
		public AbstractInterval visit(PatternValueSpace valueSpace) {
			return null;
		}

		@Override
		public AbstractInterval visit(BinaryValue value) {
			return null;
		}

		@Override
		public AbstractInterval visit(DateTimeValue value) {
			return null;
		}

		@Override
		public AbstractInterval visit(LiteralValue value) {
			return null;
		}

		@Override
		public AbstractInterval visit(NumericValue value) {
			return value;
		}
	}
}
