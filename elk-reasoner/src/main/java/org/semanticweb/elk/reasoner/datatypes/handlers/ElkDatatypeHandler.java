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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingUnsupportedException;

/**
 * Converts the given {@link ElkDataRange} or {@link ElkLiteral}
 * into an instance of {@link ValueSpace} by first selecting the suitable
 * {@link DatatypeHandler}.
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class ElkDatatypeHandler {
	
	private final DatatypeHandlerFactory handlerFactory_ = new DatatypeHandlerFactory();

	private ElkDatatypeHandler() {
	}

	private static ElkDatatypeHandler instance_;

	public static ElkDatatypeHandler getInstance() {
		if (instance_ == null) {
			instance_ = new ElkDatatypeHandler();
		}
		return instance_;
	}

	public ValueSpace<?> createValueSpace(ElkLiteral literal) {
		DatatypeHandler handler = getDatatypeHandler(literal.getDatatype());
		
		return handler.createValueSpace(literal);
	}
	
	public ValueSpace<?> createValueSpace(ElkDataRange dataRange) {
		return dataRange.accept(new ElkDataRangeVisitor<ValueSpace<?>>() {

			@Override
			public ValueSpace<?> visit(ElkDataComplementOf elkDataComplementOf) {
				throw new ElkIndexingUnsupportedException(elkDataComplementOf);
			}

			@Override
			public ValueSpace<?> visit(
					ElkDataIntersectionOf elkDataIntersectionOf) {
				throw new ElkIndexingUnsupportedException(elkDataIntersectionOf);
			}

			@Override
			public ValueSpace<?> visit(ElkDataOneOf elkDataOneOf) {
				List<? extends ElkLiteral> literals = elkDataOneOf.getLiterals();
				
				if (literals.size() != 1) {
					throw new ElkIndexingUnsupportedException(elkDataOneOf);
				}
				else {
					return createValueSpace(literals.get(0));
				}
			}

			@Override
			public ValueSpace<?> visit(ElkDatatype elkDatatype) {
				DatatypeHandler handler = getDatatypeHandler(elkDatatype);
				
				return handler.createValueSpace(elkDatatype);
			}

			@Override
			public ValueSpace<?> visit(
					ElkDatatypeRestriction elkDatatypeRestriction) {
				DatatypeHandler handler = getDatatypeHandler(elkDatatypeRestriction.getDatatype());
				
				return handler.createValueSpace(elkDatatypeRestriction);
			}

			@Override
			public ValueSpace<?> visit(ElkDataUnionOf elkDataUnionOf) {
				throw new ElkIndexingUnsupportedException(elkDataUnionOf);
			}
			
		});
	}
	
	private DatatypeHandler getDatatypeHandler(ElkDatatype datatype) {
		return datatype.accept(handlerFactory_);
	}
}
