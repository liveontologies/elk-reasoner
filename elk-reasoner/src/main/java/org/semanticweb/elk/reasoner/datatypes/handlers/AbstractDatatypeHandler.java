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

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
abstract class AbstractDatatypeHandler implements DatatypeHandler {

	protected abstract ElkDatatypeVisitor<ValueSpace<?>> getLiteralConverter(ElkLiteral literal);
	protected abstract ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter();
	
	@Override
	public ValueSpace<?> createValueSpace(ElkLiteral literal) {
		return literal.getDatatype().accept(getLiteralConverter(literal));
	}

	@Override
	public ValueSpace<?> createValueSpace(ElkDataRange dataRange) {
		ValueSpace<?> vs = dataRange.accept(getDataRangeConverter());
		
		return vs.isEmpty() ? EmptyValueSpace.INSTANCE : vs;
	}

}
