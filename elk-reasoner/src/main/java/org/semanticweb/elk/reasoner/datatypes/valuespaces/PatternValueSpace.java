/*
 * #%L
 * ELK Reasoner
 * 
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
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

import java.util.regex.Pattern;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;

/**
 * Representation of any value that satisfies specified pattern
 * 
 * @author Pospishnyi Olexandr
 */
public class PatternValueSpace implements ValueSpace {

	public Pattern pattern;
	public Datatype datatype;

	public PatternValueSpace(String regexp, Datatype datatype) {
		try {
			this.datatype = datatype;
			this.pattern = Pattern.compile(regexp);
		} catch (Throwable th) {
		}
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public ValueSpaceType getType() {
		return ValueSpaceType.PATTERN;
	}

	public boolean isEmptyInterval() {
		return pattern != null;
	}

	/**
	 * PatternValueSpace could contain
	 * - another PatternValueSpace if both are equal 
	 * - LiteralValueSpace that satisfies pattern
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	public boolean contains(ValueSpace valueSpace) {
		boolean typechek = valueSpace.getDatatype().isCompatibleWith(this.datatype);
		if (typechek != true) {
			return false;
		}
		switch (valueSpace.getType()) {
			case LITERAL:
				LiteralValueSpace lvs = (LiteralValueSpace) valueSpace;
				return pattern.matcher(lvs.value).matches();
			case PATTERN:
				PatternValueSpace pvs = (PatternValueSpace) valueSpace;
				return this.pattern.pattern().equals(pvs.pattern.pattern());
			default:
				return false;
		}
	}
}
