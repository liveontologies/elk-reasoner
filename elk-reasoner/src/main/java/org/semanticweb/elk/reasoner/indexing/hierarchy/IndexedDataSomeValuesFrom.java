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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.reasoner.datatypes.DatatypeEngine;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataSomeValuesFromVisitor;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class IndexedDataSomeValuesFrom extends IndexedDatatypeExpression {

	protected final ElkDataRange filler;
	protected final Datatype datatype;

	protected IndexedDataSomeValuesFrom(IndexedDataProperty dataProperty, ElkDataRange dataRange) {
		super(dataProperty);
		this.filler = dataRange;
		if (filler instanceof ElkDatatype) {
			datatype = Datatype.getByIri(((ElkDatatype) filler).getDatatypeIRI());
		} else {
			datatype = Datatype.getByIri(((ElkDatatypeRestriction) filler).getDatatype().getDatatypeIRI());
		}
	}

	public ElkDataRange getFiller() {
		return filler;
	}

	@Override
	public Datatype getDatatype() {
		return datatype;
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {
		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			DatatypeEngine.register(property, this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			DatatypeEngine.unregister(property, this);
		}
	}

	public <O> O accept(IndexedDataSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedDataSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	public String toString() {
		return "DataSomeValuesFrom(<" + this.property.getIri().getFullIriAsString() + "> " + filler.toString() + ")";
	}
}
