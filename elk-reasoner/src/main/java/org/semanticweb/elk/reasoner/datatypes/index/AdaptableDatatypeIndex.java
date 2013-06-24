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
import org.semanticweb.elk.reasoner.saturation.rules.DatatypeRule;

/**
 *
 * @author Pospishnyi Oleksandr
 */
public class AdaptableDatatypeIndex implements DatatypeIndex {

	private final SimpleDatatypeIndex simpleDatatypeIndex;
	private final IntervalTreeDatatypeIndex treeDatatypeIndex;
	private final IndexSelector indexSelector;

	public AdaptableDatatypeIndex() {
		this.indexSelector = new IndexSelector();
		this.simpleDatatypeIndex = new SimpleDatatypeIndex();
		this.treeDatatypeIndex= new IntervalTreeDatatypeIndex();
	}

	@Override
	public void addDatatypeRule(DatatypeRule rule) {
		rule.getValueSpace().accept(indexSelector).addDatatypeRule(rule);
	}

	@Override
	public boolean removeDatatypeRule(DatatypeRule rule) {
		return rule.getValueSpace().accept(indexSelector).removeDatatypeRule(rule);
	}

	@Override
	public Collection<DatatypeRule> getDatatypeRulesFor(IndexedDatatypeExpression ide) {
		return ide.getValueSpace().accept(indexSelector).getDatatypeRulesFor(ide);
	}

	private class IndexSelector implements ValueSpaceVisitor<DatatypeIndex> {

		@Override
		public DatatypeIndex visit(EntireValueSpace valueSpace) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(EmptyValueSpace valueSpace) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(DateTimeIntervalValueSpace valueSpace) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(LengthRestrictedValueSpace valueSpace) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(NumericIntervalValueSpace valueSpace) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(PatternValueSpace valueSpace) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(BinaryValue value) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(DateTimeValue value) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(LiteralValue value) {
			return simpleDatatypeIndex;
		}

		@Override
		public DatatypeIndex visit(NumericValue value) {
			return simpleDatatypeIndex;
		}
	}
}
