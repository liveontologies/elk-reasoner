/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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

import java.util.Collections;
import java.util.List;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.DatatypeRestriction;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit.Domain;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit.Relation;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataHasValueVisitor;

public class IndexedDataHasValue extends IndexedDatatypeExpression {

	protected final ElkLiteral filler;
	protected List<DatatypeRestriction> dtRestrictions;
	protected Domain dtDomain;

	protected IndexedDataHasValue(IndexedDataProperty dataProperty, ElkLiteral elkLiteral) {
		super(dataProperty);
		this.filler = elkLiteral;
		dtDomain = DatatypeToolkit.clarifyDomain(filler.getDatatype());
		DatatypeRestriction restriction = new DatatypeRestriction(
				Relation.EQUAL,
				filler.getLexicalForm(),
				dtDomain);
		dtRestrictions = Collections.singletonList(restriction);
	}

	public ElkLiteral getFiller() {
		return filler;
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {
		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			property.addNegExistential(this);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			property.removeNegExistential(this);
		}
	}

	public <O> O accept(IndexedDataHasValueVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedDataHasValueVisitor<O>) visitor);
	}

	@Override
	public String toString() {
		return "DataHasValue(" + '<' + this.property.getIri().asString()
				+ "> \"" + this.filler.getLexicalForm() + "\"^^<"
				+ this.filler.getDatatype().getIri().asString() + ">)";
	}

	@Override
	public List<DatatypeRestriction> getRestrictions() {
		return dtRestrictions;
	}

	@Override
	public int getRestrictionCount() {
		return 1;
	}

	@Override
	public Domain getRestrictionDomain() {
		return dtDomain;
	}
}
