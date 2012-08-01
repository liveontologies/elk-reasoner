/*
 * #%L
 * ELK Reasoner
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicOperations;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.LiteralValue;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Representation of any value that satisfies specified regular expression
 *
 * @author Pospishnyi Olexandr
 */
public class PatternValueSpace implements ValueSpace {

	public Automaton automaton;
	public ELDatatype datatype;
	public ELDatatype effectiveDatatype;

	public PatternValueSpace(Automaton automaton, ELDatatype datatype, ELDatatype effectiveDatatype) {
		this.datatype = datatype;
		this.automaton = automaton;
		this.effectiveDatatype = effectiveDatatype;
	}

	@Override
	public ELDatatype getDatatype() {
		return effectiveDatatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.PATTERN;
	}

	@Override
	public boolean isEmptyInterval() {
		return automaton.isEmpty() || !effectiveDatatype.isCompatibleWith(datatype);
	}

	/**
	 * PatternValueSpace could contain
	 * - another PatternValueSpace if one is a subset of another
	 * - LengthRestrictedValueSpace that will satisfy this pattern
	 * - LiteralValue that matches pattern
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 *
	 * Note: BasicOperations.subsetOf() appears to be not thread safe.
	 * Cloning initial automatons to avoid ConcurrentModificationException.
	 * Todo: synchronize this block is performance well be an issues
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		boolean typechek = valueSpace.getDatatype().isCompatibleWith(this.effectiveDatatype);
		if (typechek != true) {
			return false;
		}
		switch (valueSpace.getType()) {
			case LITERAL_VALUE:
				LiteralValue lvs = (LiteralValue) valueSpace;
				return automaton.run(lvs.value);
			case PATTERN:
				PatternValueSpace pvs = (PatternValueSpace) valueSpace;
				return BasicOperations.subsetOf(pvs.automaton.clone(), this.automaton.clone());
			case LENGTH_RESTRICTED:
				LengthRestrictedValueSpace lrvs = (LengthRestrictedValueSpace) valueSpace;
				return BasicOperations.subsetOf(lrvs.asAutomaton().clone(), this.automaton.clone());
			default:
				return false;
		}
	}

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof PatternValueSpace) {
			PatternValueSpace otherEntry = (PatternValueSpace) other;
			return this.datatype.equals(otherEntry.datatype)
				&& this.effectiveDatatype.equals(otherEntry.effectiveDatatype)
				&& this.automaton.equals(otherEntry.automaton);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			PatternValueSpace.class,
			this.datatype,
			this.effectiveDatatype,
			this.automaton
			);
	}
}
