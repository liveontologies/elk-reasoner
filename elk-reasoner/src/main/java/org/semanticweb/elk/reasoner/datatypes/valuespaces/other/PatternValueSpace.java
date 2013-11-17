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

/**
 * Representation of any value that satisfies specified regular expression
 *
 * @author Pospishnyi Olexandr
 */
public class PatternValueSpace implements ValueSpace<ElkDatatype> {

	private final Automaton automaton_;
	private final ElkDatatype datatype_;

	public PatternValueSpace(Automaton automaton, ElkDatatype datatype) {
		this.datatype_ = datatype;
		this.automaton_ = automaton;
	}

	Automaton getAutomaton() {
		return automaton_;
	}
	
	@Override
	public ElkDatatype getDatatype() {
		return datatype_;
	}

	@Override
	public boolean isEmpty() {
		return automaton_.isEmpty();
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
	public boolean contains(ValueSpace<?> valueSpace) {
		
		return valueSpace.getDatatype().isCompatibleWith(datatype_) && 
				valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(LengthRestrictedValueSpace lrvs) {
				return BasicOperations.subsetOf(lrvs.asAutomaton().clone(), automaton_.clone());
			}

			@Override
			public Boolean visit(PatternValueSpace pvs) {
				return BasicOperations.subsetOf(pvs.automaton_.clone(), automaton_.clone());
			}

			@Override
			public Boolean visit(LiteralValue lvs) {
				return automaton_.run(lvs.getString());
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
		if (other instanceof PatternValueSpace) {
			PatternValueSpace otherEntry = (PatternValueSpace) other;
			return this.datatype_.equals(otherEntry.datatype_)
				&& this.automaton_.equals(otherEntry.automaton_);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			PatternValueSpace.class,
			this.datatype_,
			this.automaton_
			);
	}

	@Override
	public String toString() {
		return datatype_.toString() + " pattern: \"" + automaton_.getInfo() + "\"";
	}
	
	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
