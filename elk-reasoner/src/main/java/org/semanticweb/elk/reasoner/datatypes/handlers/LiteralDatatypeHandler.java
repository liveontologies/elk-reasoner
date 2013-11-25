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
package org.semanticweb.elk.reasoner.datatypes.handlers;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkUnexpectedIndexingException;

/**
 * rdfs:Literal datatype handler.
 * <p>
 * This datatype has no practical usage as it has neither normative facets nor
 * lexical representation. Could only participate in DataSomeValuesFrom
 * expressions.
 * <p>
 * Uses {@link EntireValueSpace} value space restriction only.
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
public class LiteralDatatypeHandler extends AbstractDatatypeHandler {

	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseDataRangeConverter() {
		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return EntireValueSpace.RDFS_LITERAL;
		}
	};
	
	
	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() {
		return dataRangeConverter_;
	}


	@Override
	public PointValue<?, ?> createValueSpace(ElkLiteral literal) {
		throw new ElkUnexpectedIndexingException("There's no lexical representation of rdfs:Literal in OWL 2");
	}
}
