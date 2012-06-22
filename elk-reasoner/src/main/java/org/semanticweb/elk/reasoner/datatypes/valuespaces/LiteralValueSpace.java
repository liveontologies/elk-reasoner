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

import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.util.collections.Pair;

/**
 * Value space that represent single literal value. 
 * 
 * @author Pospishnyi Olexandr
 */
public class LiteralValueSpace implements ValueSpace {

	public String value;
	public String language;
	public Datatype datatype;
	public Datatype effectiveDatatype;

	public LiteralValueSpace(Pair<String, String> pair, Datatype datatype, Datatype effectiveDatatype) {
		this.value = pair.getFirst();
		this.language = pair.getSecond();
		this.datatype = datatype;
		this.effectiveDatatype = effectiveDatatype;
	}

	public LiteralValueSpace(String string, Datatype datatype, Datatype effectiveDatatype) {
		this.value = string;
		this.datatype = datatype;
		this.effectiveDatatype = effectiveDatatype;
	}

	public Datatype getDatatype() {
		return effectiveDatatype;
	}

	public ValueSpaceType getType() {
		return ValueSpaceType.LITERAL;
	}

	public boolean isEmptyInterval() {
		return !effectiveDatatype.isCompatibleWith(datatype);
	}

	/*
	 * Two literals are equal if and only if all of the following hold:
	 *
	 * 1. The strings of the two lexical forms compare equal, character by
	 * character. 2. Either both or neither have language tags. 3. The language
	 * tags, if any, compare equal. 4. Either both or neither have datatype
	 * URIs. 5. The two datatype URIs, if any, compare equal, character by
	 * character.
	 *
	 */
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
			case LITERAL:
				LiteralValueSpace other = (LiteralValueSpace) valueSpace;
				boolean result = true;
				result &= this.value.equals(other.value);
				result &= ((this.language == null && other.language == null)
						|| (this.language != null && other.language != null
						&& this.language.equals(other.language)));
				result &= this.datatype == other.datatype;
				return result;
			default:
				return false;
		}
	}
}
