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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.reasoner.datatypes.DatatypeRestriction;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit;
import org.semanticweb.elk.reasoner.datatypes.DatatypeToolkit.Domain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataSomeValuesFromVisitor;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class IndexedDataSomeValuesFrom extends IndexedDatatypeExpression {

	protected final ElkDataRange filler;
	protected boolean headlessRestriction = false;
	protected List<DatatypeRestriction> dtRestrictions;
	protected Domain dtDomain;

	protected IndexedDataSomeValuesFrom(IndexedDataProperty dataProperty, ElkDataRange dataRange) {
		super(dataProperty);
		headlessRestriction = dataRange instanceof ElkDatatype;
		this.filler = dataRange;
		if (headlessRestriction) {
			dtDomain = DatatypeToolkit.clarifyDomain((ElkDatatype) this.filler);
			dtRestrictions = Collections.singletonList(new DatatypeRestriction(null, null, dtDomain));
		} else {
			ElkDatatypeRestriction dtr = (ElkDatatypeRestriction) this.filler;
			dtRestrictions = new ArrayList<DatatypeRestriction>(dtr.getFacetRestrictions().size());
			for (ElkFacetRestriction facetRestriction : dtr.getFacetRestrictions()) {
				if (dtDomain == null) {
					dtDomain = DatatypeToolkit.clarifyDomain(dtr.getDatatype());
				}
				DatatypeRestriction restriction = new DatatypeRestriction(
						DatatypeToolkit.clarifyRelation(facetRestriction.getConstrainingFacet()),
						facetRestriction.getRestrictionValue().getLexicalForm(),
						dtDomain);
				dtRestrictions.add(restriction);
			}
		}
	}

	public ElkDataRange getFiller() {
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

	public <O> O accept(IndexedDataSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedDataSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	public String toString() {
		return "DataSomeValuesFrom(<" + this.property.getIri().asString() + "> " + filler.toString() + ")";
	}

	@Override
	public List<DatatypeRestriction> getRestrictions() {
		return dtRestrictions;
	}

	@Override
	public int getRestrictionCount() {
		return headlessRestriction ? 0 : dtRestrictions.size();
	}

	@Override
	public Domain getRestrictionDomain() {
		return dtDomain;
	}
}
