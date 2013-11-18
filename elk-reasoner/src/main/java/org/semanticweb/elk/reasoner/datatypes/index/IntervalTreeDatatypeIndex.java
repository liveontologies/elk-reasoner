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

import org.semanticweb.elk.reasoner.datatypes.util.NumberUtils;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.BaseIntervalValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Condition;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;

/**
 * An implementation of {@link DatatypeIndex} which uses an interval tree for
 * indexing indexed datatype expressions.
 * 
 * @author Pospishnyi Oleksandr
 * @author Pavel Klinov
 */
class IntervalTreeDatatypeIndex implements DatatypeIndex {

	private IntervalTree<Interval<Number>, IndexedDatatypeExpression, Number> intervalTree_;

	@Override
	public void addDatatypeExpression(final IndexedDatatypeExpression ide) {
		if (intervalTree_ == null) {
			intervalTree_ = new IntervalTree<Interval<Number>, IndexedDatatypeExpression, Number>(NumberUtils.COMPARATOR);
		}
		
		//TODO again, better to pass parameters to the visitor instead of creating objects every time
		ide.getValueSpace().accept(new BaseIntervalValueSpaceVisitor<Void>() {

			@Override
			protected Void defaultIntervalVisit(Interval<Number> interval) {
				
				intervalTree_.add(interval, ide);
				
				return null;
			}
			
		});
	}

	@Override
	public boolean removeDatatypeExpression(final IndexedDatatypeExpression ide) {
		return ide.getValueSpace().accept(new BaseIntervalValueSpaceVisitor<Boolean>() {

			@Override
			protected Boolean defaultIntervalVisit(Interval<Number> interval) {
				return intervalTree_.remove(interval, ide);
			}
			
		});
	}

	@Override
	public Iterable<IndexedDatatypeExpression> getSubsumersFor(
			final IndexedDatatypeExpression ide) {

		final ValueSpace<?> valueSpace = ide.getValueSpace();
		
		if (intervalTree_ == null) {
			return Collections.emptyList();
		}

		Collection<IndexedDatatypeExpression> ret = valueSpace.accept(new BaseIntervalValueSpaceVisitor<Collection<IndexedDatatypeExpression>>() {

			@Override
			protected Collection<IndexedDatatypeExpression> defaultVisit(
					ValueSpace<?> valueSpace) {
				return Collections.emptyList();
			}

			@Override
			protected Collection<IndexedDatatypeExpression> defaultIntervalVisit(
					Interval<Number> interval) {
				return intervalTree_.searchIncludes(interval);
			}
			
		});

		// final type filtering, required because interval search does not take
		// in account datatypes so, for example, an xsd:integer interval can be
		// returned as a super-interval for owl:rational
		// if it's wider boundary-wise.
		return Operations.filter(ret, new Condition<IndexedDatatypeExpression>() {

			@Override
			public boolean holds(IndexedDatatypeExpression subsumer) {
				return valueSpace.getDatatype().isCompatibleWith(subsumer.getValueSpace().getDatatype());
			}
			
		});
	}

	@Override
	public void appendTo(DatatypeIndex index) {
		for (IndexedDatatypeExpression ide : intervalTree_.values()) {
			index.addDatatypeExpression(ide);
		}
	}

}
