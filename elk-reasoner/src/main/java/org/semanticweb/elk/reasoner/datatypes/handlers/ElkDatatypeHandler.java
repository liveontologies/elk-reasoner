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
	
	private ElkDatatypeHandler() {
	}
	
	private static ElkDatatypeHandler instance_;

	public static ElkDatatypeHandler getInstance() {
		if (instance_ == null) {
			instance_ = new ElkDatatypeHandler();
		}
		return instance_;
	}

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
}
