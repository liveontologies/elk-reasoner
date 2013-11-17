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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.other;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.BaseValueSpaceContainmentVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.Datatypes;

/**
 * Representation of any value that satisfies specified length
 * 
 * @author Pospishnyi Olexandr
 */
public class LengthRestrictedValueSpace implements ValueSpace<ElkDatatype> {

	private final Integer minLength;
	private final Integer maxLength;
	private final ElkDatatype datatype;
	private final Automaton automaton;

	public LengthRestrictedValueSpace(ElkDatatype datatype,
			Integer minLength, Integer maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.datatype = datatype;
		Automaton anyCharAutomaton = Datatypes.get("Char");

		if (maxLength == Integer.MAX_VALUE) {
			if (minLength == 0) {
				automaton = anyCharAutomaton.repeat();
			} else {
				automaton = BasicOperations.repeat(anyCharAutomaton, minLength);
			}
		} else {
			automaton = BasicOperations.repeat(anyCharAutomaton, minLength,
					maxLength);
		}
	}

	@Override
	public ElkDatatype getDatatype() {
		return datatype;
	}

	@Override
	public boolean isEmpty() {
		if (minLength == null && maxLength == null) {
			return true;
		}
		if (minLength != null && minLength < 0) {
			return true;
		}
		if (maxLength != null && maxLength < 0) {
			return true;
		}
		if (minLength != null && maxLength != null && minLength > maxLength) {
			return true;
		}
		return false;
	}

	/**
	 * Finite-state automaton that represents literal length restriction. Used
	 * to deduce subsumption between LengthRestrictedValueSpace and
	 * PatternValueSpace
	 * 
	 * @return Automaton
	 */
	public Automaton asAutomaton() {
		return automaton;
	}

	/**
	 * LengthRestrictedValueSpace could contain - another
	 * LengthRestrictedValueSpace within this one - LiteralValue that satisfies
	 * length restrictions - PatternValueSpace that will satisfy length
	 * restrictions - BinaryValue that satisfies length restrictions
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace<?> valueSpace) {

		return valueSpace.getDatatype().isCompatibleWith(datatype) && 
				valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(LengthRestrictedValueSpace lrvs) {
				return minLength.compareTo(lrvs.minLength) <= 0
						&& maxLength.compareTo(lrvs.maxLength) >= 0;
			}

			@Override
			public Boolean visit(PatternValueSpace pvs) {
				return BasicOperations.subsetOf(pvs.getAutomaton().clone(),
						asAutomaton().clone());
			}

			@Override
			public Boolean visit(BinaryValue bvs) {
				return minLength.compareTo(bvs.value.length) <= 0
						&& maxLength.compareTo(bvs.value.length) >= 0;
			}

			@Override
			public Boolean visit(LiteralValue lvs) {
				int len = lvs.getString().length();

				return minLength.compareTo(len) <= 0
						&& maxLength.compareTo(len) >= 0;
			}

		});
	}

	@Override
	public boolean isSubsumedBy(ValueSpace<?> valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof LengthRestrictedValueSpace) {
			LengthRestrictedValueSpace otherEntry = (LengthRestrictedValueSpace) other;
			return this.datatype.equals(otherEntry.datatype)
					&& this.minLength.equals(otherEntry.minLength)
					&& this.maxLength.equals(otherEntry.maxLength);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(LengthRestrictedValueSpace.class,
				this.datatype, this.minLength, this.maxLength);
	}

	@Override
	public String toString() {
		return datatype.toString() + " length: >=" + minLength + " <="
				+ maxLength;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
