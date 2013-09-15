package org.semanticweb.elk.reasoner.datatypes.handlers;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import org.semanticweb.elk.owl.datatypes.*;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.visitors.ElkDatatypeParser;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingUnsupportedException;

/**
 * @author Pospishnyi Olexandr
 */
public abstract class AbstractDatatypeHandler implements DatatypeHandler {

	@Override
	public ValueSpace visit(ElkDataUnionOf elkDataUnionOf) {
		throw new ElkIndexingUnsupportedException(elkDataUnionOf);
	}

	@Override
	public ValueSpace visit(ElkDataComplementOf elkDataComplementOf) {
		throw new ElkIndexingUnsupportedException(elkDataComplementOf);
	}

	@Override
	public ValueSpace visit(ElkDataIntersectionOf elkDataIntersectionOf) {
		throw new ElkIndexingUnsupportedException(elkDataIntersectionOf);
	}

	@Override
	public ValueSpace visit(ElkDataOneOf elkDataOneOf) {
		throw new ElkIndexingUnsupportedException(elkDataOneOf);
	}

	protected enum Facet {
		
		MIN_INCLUSIVE ("http://www.w3.org/2001/XMLSchema#minInclusive", ">="),
		MIN_EXCLUSIVE ("http://www.w3.org/2001/XMLSchema#minExclusive", ">"),
		MAX_INCLUSIVE ("http://www.w3.org/2001/XMLSchema#maxInclusive", "<="),
		MAX_EXCLUSIVE ("http://www.w3.org/2001/XMLSchema#maxExclusive", "<"),
		MIN_LENGTH    ("http://www.w3.org/2001/XMLSchema#minLength",	"l>"),
		MAX_LENGTH    ("http://www.w3.org/2001/XMLSchema#maxLength",	"l<"),
		LENGTH        ("http://www.w3.org/2001/XMLSchema#length",	"l="),
		PATTERN       ("http://www.w3.org/2001/XMLSchema#pattern",	"regex:");
		
		public final String iri;
		public final String symbol;

		private Facet(String iri, String symbol) {
			this.iri = iri;
			this.symbol = symbol;
		}

		public static Facet getByIri(String iri) {
			for (Facet facet : Facet.values()) {
				if (iri.equals(facet.iri)) {
					return facet;
				}
			}
			return null;
		}
	}

	abstract protected class DatatypeValueParser<O, P> implements ElkDatatypeParser<O, P> {

		@Override
		public O parse(LiteralDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(DateTimeDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(DateTimeStampDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(Base64BinaryDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(HexBinaryDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(AnyUriDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(RealDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(RationalDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(DecimalDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(IntegerDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(NonNegativeIntegerDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(PlainLiteralDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(StringDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(NormalizedStringDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(TokenDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(NameDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(NcNameDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(NmTokenDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(XmlLiteralDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}

		@Override
		public O parse(UndefinedDatatype datatype, P param) {
			throw new ElkIndexingUnsupportedException(datatype);
		}
	}
}
