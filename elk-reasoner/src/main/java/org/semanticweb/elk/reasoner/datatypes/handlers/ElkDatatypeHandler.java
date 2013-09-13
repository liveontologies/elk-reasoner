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

import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingUnsupportedException;

/**
 * @author Pospishnyi Olexandr
 */
public class ElkDatatypeHandler implements DatatypeHandler {

	private final DatatypeHandlerFactory handlerFactory = new DatatypeHandlerFactory();

	@Override
	public ValueSpace visit(ElkLiteral elkLiteral) {
		DatatypeHandler handler = elkLiteral.getDatatype().accept(handlerFactory);
		return elkLiteral.accept(handler);
	}

	@Override
	public ValueSpace visit(ElkDatatype elkDatatype) {
		DatatypeHandler handler = elkDatatype.accept(handlerFactory);
		return elkDatatype.accept(handler);
	}

	@Override
	public ValueSpace visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		DatatypeHandler handler = elkDatatypeRestriction.getDatatype().accept(handlerFactory);
		return elkDatatypeRestriction.accept(handler);
	}

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
}
