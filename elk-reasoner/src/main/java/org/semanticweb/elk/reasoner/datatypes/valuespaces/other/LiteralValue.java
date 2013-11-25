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
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Value space that represent single literal value.
 * 
 * @author Pospishnyi Olexandr
 */
public class LiteralValue implements PointValue<ElkDatatype, String[]> {

	private String string_;
	private String language_;
	private ElkDatatype datatype_;

	public LiteralValue(String string, String language, ElkDatatype datatype) {
		this.string_ = string;
		this.language_ = language;
		this.datatype_ = datatype;
	}

	public LiteralValue(String string, ElkDatatype datatype) {
		this.string_ = string;
		this.datatype_ = datatype;
	}

	@Override
	public ElkDatatype getDatatype() {
		return datatype_;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
	
	public String getString() {
		return string_;
	}

	@Override
	public boolean contains(ValueSpace<?> valueSpace) {
		return valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(LiteralValue string) {
				return typeSafeEquals(string);
			}

		});
	}
	
	/*
	 * Two literals are equal if and only if all of the following hold:
	 * 
	 * 1. The strings of the two lexical forms compare equal, character by
	 * character. 2. Either both or neither have language tags. 3. The language
	 * tags, if any, compare equal. 4. Either both or neither have datatype
	 * URIs. 5. The two datatype URIs, if any, compare equal, character by
	 * character.
	 */
	private boolean typeSafeEquals(LiteralValue string) {
		boolean result = true;

		result &= string_.equals(string.string_);
		result &= ((language_ == null && string.language_ == null) || (language_ != null
				&& string.language_ != null && language_.equals(string.language_)));
		result &= datatype_ == string.datatype_;

		return result;
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
		if (other instanceof LiteralValue) {
			return typeSafeEquals((LiteralValue) other);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(LiteralValue.class,
				this.datatype_, this.string_,
				this.language_ != null ? this.language_ : 0);
	}

	@Override
	public String toString() {
		return "\"" + this.string_ + "@" + (language_ != null ? language_ : "")
				+ "\"^^" + datatype_;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String[] getValue() {
		return language_ == null ? new String[] {string_} : new String[]{string_, language_};
	}
}
