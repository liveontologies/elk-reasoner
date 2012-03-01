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
package org.semanticweb.elk.reasoner.rules;

import java.util.Date;
import javax.xml.bind.DatatypeConverter;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.rules.DatatypeResolutionEngine.Domain;
import org.semanticweb.elk.reasoner.rules.DatatypeResolutionEngine.Relation;

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

	public DatatypeRestriction(ElkDatatype datatype, Relation relation, String value) {
		this.relation = relation;
		this.value = value;
		this.domain = clarifyDomain(datatype);
	}

	public DatatypeRestriction(ElkDatatype datatype, String relation, String value) {
		this.value = value;
		this.relation = clarifyRelation(relation);
		this.domain = clarifyDomain(datatype);
	}

	public DatatypeRestriction(Relation relation, String value, Domain domain) {
		this.relation = relation;
		this.value = value;
		this.domain = domain;
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

	public Date getValueAsDate() {
		try {
			switch (domain) {
				case DATE:
					return DatatypeConverter.parseDate(value).getTime();
				case TIME:
					return DatatypeConverter.parseTime(value).getTime();
				case DATETIME:
					return DatatypeConverter.parseDateTime(value).getTime();
				default:
					return null;
			}

		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public Number getValueAsNumber() {
		switch (domain) {
			case N:
			case Z:
				return Long.valueOf(value);
			case R:
				return Double.valueOf(value);
			default:
				return null;
		}
	}

	private static Relation clarifyRelation(String relation) {
		if (relation == null || relation.length() == 0) {
			return null;
		}
		if ("minInclusive".equals(relation)) {
			return Relation.MORE_OR_EQUAL;
		} else if ("minExclusive".equals(relation)) {
			return Relation.MORE;
		} else if ("maxExclusive".equals(relation)) {
			return Relation.LESS;
		} else if ("maxInclusive".equals(relation)) {
			return Relation.LESS_OR_EQUAL;
		} else {
			return null;
		}
	}

	private static Domain clarifyDomain(ElkDatatype datatype) {
		if (datatype == null) {
			return null;
		}
		String dt = datatype.getDatatypeShortname();
		if ("string".equals(dt) || "PlainLiteral".equals(dt)) {
			return Domain.TEXT;
		} else if ("integer".equals(dt) || "nonPositiveInteger".equals(dt)
				|| "negativeInteger".equals(dt) || "long".equals(dt)
				|| "int".equals(dt) || "short".equals(dt) || "byte".equals(dt)) {
			return Domain.Z;
		} else if ("nonNegativeInteger".equals(dt) || "positiveInteger".equals(dt)
				|| "unsignedLong".equals(dt) || "unsignedInt".equals(dt)
				|| "unsignedShort".equals(dt) || "unsignedByte".equals(dt)) {
			return Domain.N;
		} else if ("double".equals(dt) || "float".equals(dt) || "decimal".equals(dt)) {
			return Domain.R;
		} else if ("date".equals(dt)) {
			return Domain.DATE;
		} else if ("time".equals(dt)) {
			return Domain.TIME;
		} else if ("datetime".equals(dt)) {
			return Domain.DATETIME;
		} else {
			return Domain.OTHER;
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
