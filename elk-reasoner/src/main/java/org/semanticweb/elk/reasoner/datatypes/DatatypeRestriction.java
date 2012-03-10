/*
 * Copyright 2012 Department of Computer Science, University of Oxford.
 *
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
 */
package org.semanticweb.elk.reasoner.datatypes;

import javax.xml.bind.DatatypeConverter;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit.Domain;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit.Relation;

/**
 * An entity that represents a datatype restriction
 *
 * @author Pospishnyi Olexandr
 */
public class DatatypeRestriction {

	public Relation relation;
	public String value;
	public Domain domain;

	public DatatypeRestriction() {
	}

	public DatatypeRestriction(Relation relation, String value, Domain domain) {
		this.relation = relation;
		this.value = value;
		this.domain = domain;
		//make catch-all regular expression for TEXT domain
		//-Inf...+Inf will be asumed for numeric and time domains
		if (value == null && domain == Domain.TEXT) {
			this.relation = Relation.EQUAL;
			this.value = ".*";
		}
	}

	public Domain getDomain() {
		return domain;
	}

	public Relation getRelation() {
		return relation;
	}

	public String getValueAsString() {
		return value;
	}

	public Number getValueAsNumber() {
		switch (domain) {
			case N:
			case Z:
				return Long.valueOf(value);
			case R:
				return Double.valueOf(value);
			case DATE:
				return DatatypeConverter.parseDate(value).getTime().getTime();
			case TIME:
				return DatatypeConverter.parseTime(value).getTime().getTime();
			case DATETIME:
				return DatatypeConverter.parseDateTime(value).getTime().getTime();
			default:
				return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DatatypeRestriction other = (DatatypeRestriction) obj;
		if (this.relation != other.relation || this.domain != other.domain) {
			return false;
		}
		if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
			return false;
		}
		return true;
	}
}
