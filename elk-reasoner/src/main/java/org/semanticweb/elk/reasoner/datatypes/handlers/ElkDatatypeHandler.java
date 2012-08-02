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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDataValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexingException;

/**
 * @author Pospishnyi Olexandr
 */
public class ElkDatatypeHandler implements DatatypeHandler, ElkDataValueSpaceVisitor<ValueSpace> {

	private static final Map<ELDatatype, ElkDatatypeHandler> datatypeHandlers =
		new EnumMap<ELDatatype, ElkDatatypeHandler>(ELDatatype.class);

	static {
		registerDatatypeHandler(new AnyURIDatatypeHandler());
		registerDatatypeHandler(new BinaryDatatypeHandler());
		registerDatatypeHandler(new DateTimeDatatypeHandler());
		registerDatatypeHandler(new LiteralDatatypeHandler());
		registerDatatypeHandler(new NumericDatatypeHandler());
		registerDatatypeHandler(new PlainLiteralDatatypeHandler());
		registerDatatypeHandler(new XMLLiteralDatatypeHandler());
	}

	private static void registerDatatypeHandler(ElkDatatypeHandler handler) {
		for (ELDatatype datatype : handler.getSupportedDatatypes()) {
			datatypeHandlers.put(datatype, handler);
		}
	}

	private static ElkDatatypeHandler getDatatypeHandler(ElkDatatype datatype) {
		ElkDatatypeHandler dh = datatypeHandlers.get(datatype.asELDatatype());
		if (dh != null) {
			return dh;
		} else {
			throw new IndexingException(datatype);
		}
	}

	@Override
	public ValueSpace visit(ElkLiteral elkLiteral) {
		ValueSpace vs = getDatatypeHandler(elkLiteral.getDatatype()).visit(elkLiteral);
		if (vs != null) {
			return vs;
		} else {
			throw new IndexingException(elkLiteral);
		}
	}

	@Override
	public ValueSpace visit(ElkDatatype elkDatatype) {
		if (elkDatatype.asELDatatype() != null) {
			return new EntireValueSpace(elkDatatype.asELDatatype());
		} else {
			throw new IndexingException(elkDatatype);
		}
	}

	@Override
	public ValueSpace visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		ValueSpace vs = getDatatypeHandler(elkDatatypeRestriction.getDatatype()).visit(elkDatatypeRestriction);
		if (vs != null) {
			return vs;
		} else {
			throw new IndexingException(elkDatatypeRestriction);
		}
	}

	@Override
	public ValueSpace visit(ElkDataUnionOf elkDataUnionOf) {
		throw new IndexingException(elkDataUnionOf);
	}

	@Override
	public ValueSpace visit(ElkDataComplementOf elkDataComplementOf) {
		throw new IndexingException(elkDataComplementOf);
	}

	@Override
	public ValueSpace visit(ElkDataIntersectionOf elkDataIntersectionOf) {
		throw new IndexingException(elkDataIntersectionOf);
	}

	@Override
	public ValueSpace visit(ElkDataOneOf elkDataOneOf) {
		throw new IndexingException(elkDataOneOf);
	}

	@Override
	public Set<ELDatatype> getSupportedDatatypes() {
		return EnumSet.allOf(ELDatatype.class);
	}

	@Override
	public Set<Facet> getSupportedFacets() {
		return EnumSet.allOf(Facet.class);
	}
}
