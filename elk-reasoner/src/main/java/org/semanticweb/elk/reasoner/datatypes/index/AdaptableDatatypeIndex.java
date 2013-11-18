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

import java.util.Collections;

import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireNumericValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.OtherEntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeStampInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.ArbitraryIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.IntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RealInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.BinaryValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LiteralValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.PatternValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;

/**
 * Delegates all calls to the appropriate implementation of
 * {@link DatatypeIndex} based on the kind of {@link ValueSpace} in the given
 * {@link IndexedDatatypeExpression}.
 * 
 * @author Oleksandr Pospishnyi
 */
public class AdaptableDatatypeIndex implements DatatypeIndex {

	/**
	 * A vacuous index for rdfs:Literal because it subsumes both interval and non-interval data ranges. 
	 * Strictly speaking it'd be sufficient to just maintain a flag whether rdfs:Literal occurred in the ontology but we use the index for uniformity of the representation.
	 */
	
	/**
	 * Index for datatypes for which there's no efficient indexing yet.
	 */
	private final SimpleDatatypeIndex simpleDatatypeIndex_;

	/**
	 * Index for datatypes for which we use {@link IntervalTree}s.
	 */
	private final IntervalTreeDatatypeIndex treeDatatypeIndex_;
	
	/**
	 * If rdfs:Literal occurs in the ontology (for the given property), we know
	 * that it subsumes all data ranges (for any subproperty). For uniformity
	 * reasons we may maintain a dedicated index for that case or we can just
	 * store it as a single value, as we do here.
	 */
	private IndexedDatatypeExpression rdfsLiteralExpression_;

	/**
	 * The visitor which actually selects the right index.
	 */
	private final IndexSelector indexSelector_;

	public AdaptableDatatypeIndex() {
		this.simpleDatatypeIndex_ = new SimpleDatatypeIndex();
		this.treeDatatypeIndex_ = new IntervalTreeDatatypeIndex();
		this.indexSelector_ = new IndexSelector();
	}

	@Override
	public void addDatatypeExpression(IndexedDatatypeExpression ide) {
		if (ide.getValueSpace() == EntireValueSpace.RDFS_LITERAL) {
			rdfsLiteralExpression_ = ide;
		}
		else {
			ide.getValueSpace().accept(indexSelector_).addDatatypeExpression(ide);
		}
	}

	@Override
	public boolean removeDatatypeExpression(IndexedDatatypeExpression ide) {
		if (ide.getValueSpace() == EntireValueSpace.RDFS_LITERAL) {
			if (rdfsLiteralExpression_ != null) {
				rdfsLiteralExpression_ = null;
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return ide.getValueSpace().accept(indexSelector_)
					.removeDatatypeExpression(ide);
		}
	}

	@Override
	public Iterable<IndexedDatatypeExpression> getSubsumersFor(
			IndexedDatatypeExpression ide) {
		DatatypeIndex index = ide.getValueSpace().accept(indexSelector_);
		Iterable<IndexedDatatypeExpression> ret = index.getSubsumersFor(ide);
		
		if (rdfsLiteralExpression_ != null) {
			return Operations.concat(ret, Collections.singletonList(rdfsLiteralExpression_));
		}
		else {
			return ret;
		}
	}

	@Override
	public void appendTo(DatatypeIndex index) {
		simpleDatatypeIndex_.appendTo(index);
		treeDatatypeIndex_.appendTo(index);
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class IndexSelector implements ValueSpaceVisitor<DatatypeIndex> {

		@Override
		public DatatypeIndex visit(EntireNumericValueSpace<?> valueSpace) {
			return treeDatatypeIndex_;
		}
		
		@Override
		public DatatypeIndex visit(OtherEntireValueSpace<?> valueSpace) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(EmptyValueSpace valueSpace) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(DateTimeInterval valueSpace) {
			return simpleDatatypeIndex_;
		}
		
		@Override
		public DatatypeIndex visit(DateTimeStampInterval valueSpace) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(LengthRestrictedValueSpace valueSpace) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(PatternValueSpace valueSpace) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(BinaryValue value) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(DateTimeValue value) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(LiteralValue value) {
			return simpleDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(RealInterval valueSpace) {
			return treeDatatypeIndex_;
		}
		
		@Override
		public DatatypeIndex visit(RationalInterval valueSpace) {
			return treeDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(DecimalInterval valueSpace) {
			return treeDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(ArbitraryIntegerInterval valueSpace) {
			return treeDatatypeIndex_;
		}
		
		@Override
		public DatatypeIndex visit(NonNegativeIntegerInterval valueSpace) {
			return treeDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(NonNegativeIntegerValue value) {
			return treeDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(RationalValue value) {
			return treeDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(DecimalValue value) {
			return treeDatatypeIndex_;
		}

		@Override
		public DatatypeIndex visit(IntegerValue value) {
			return treeDatatypeIndex_;
		}
	}
}
