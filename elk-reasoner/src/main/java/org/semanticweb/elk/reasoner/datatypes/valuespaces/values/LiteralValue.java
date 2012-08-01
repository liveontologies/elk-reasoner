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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.values;

import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Value space that represent single literal value.
 *
 * @author Pospishnyi Olexandr
 */
public class LiteralValue implements ValueSpace {

	public String value;
	public String language;
	public ELDatatype datatype;
	public ELDatatype effectiveDatatype;

	public LiteralValue(Pair<String, String> pair, ELDatatype datatype, ELDatatype effectiveDatatype) {
		this.value = pair.getFirst();
		this.language = pair.getSecond();
		this.datatype = datatype;
		this.effectiveDatatype = effectiveDatatype;
	}

	public LiteralValue(String string, ELDatatype datatype, ELDatatype effectiveDatatype) {
		this.value = string;
		this.datatype = datatype;
		this.effectiveDatatype = effectiveDatatype;
	}

	@Override
	public ELDatatype getDatatype() {
		return effectiveDatatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.LITERAL_VALUE;
	}

	@Override
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
	@Override
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
			case LITERAL_VALUE:
				LiteralValue other = (LiteralValue) valueSpace;
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

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof LiteralValue) {
			LiteralValue otherEntry = (LiteralValue) other;
			return this.datatype.equals(otherEntry.datatype)
				&& this.effectiveDatatype.equals(otherEntry.effectiveDatatype)
				&& this.value.equals(otherEntry.value)
				&& ((this.language == null && otherEntry.language == null)
					|| (this.language != null && otherEntry.language != null
					&& this.language.equals(otherEntry.language)));

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			LiteralValue.class,
			this.datatype,
			this.effectiveDatatype,
			this.value,
			this.language != null ? this.language : 0
			);
	}
}
