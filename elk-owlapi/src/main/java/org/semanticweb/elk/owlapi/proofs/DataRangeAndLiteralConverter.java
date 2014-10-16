/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class DataRangeAndLiteralConverter implements ElkDataRangeVisitor<OWLDataRange>, ElkLiteralVisitor<OWLLiteral> {

	private final OWLDataFactory factory_;
	
	DataRangeAndLiteralConverter(OWLDataFactory f) {
		factory_ = f;
	}
	
	@Override
	public OWLDataRange visit(ElkDataComplementOf dr) {
		return factory_.getOWLDataComplementOf(dr.getDataRange().accept(this));
	}

	@Override
	public OWLDataRange visit(ElkDataIntersectionOf dr) {
		OWLDataRange[] ranges = new OWLDataRange[dr.getDataRanges().size()];
		
		for (int i = 0; i < ranges.length; i++) {
			ranges[i] = dr.getDataRanges().get(i).accept(this);
		}
		
		return factory_.getOWLDataIntersectionOf(ranges);
	}

	@Override
	public OWLDataRange visit(ElkDataOneOf dr) {
		OWLLiteral[] literals = new OWLLiteral[dr.getLiterals().size()];
		
		for (int i = 0; i < literals.length; i++) {
			literals[i] = dr.getLiterals().get(i).accept(this);
		}
		
		return factory_.getOWLDataOneOf(literals);
	}

	@Override
	public OWLDataRange visit(ElkDatatype elkDatatype) {
		return getDatatype(elkDatatype);
	}

	@Override
	public OWLDataRange visit(ElkDatatypeRestriction dr) {
		OWLFacetRestriction[] facets = new OWLFacetRestriction[dr.getFacetRestrictions().size()];
		
		for (int i = 0; i < facets.length; i++) {
			ElkFacetRestriction facet = dr.getFacetRestrictions().get(i); 
			
			facets[i] = factory_.getOWLFacetRestriction(OWLFacet.getFacet(IRI.create(facet.getConstrainingFacet().getFullIriAsString())), facet.getRestrictionValue().accept(this));
		}
		
		return factory_.getOWLDatatypeRestriction(getDatatype(dr.getDatatype()), facets);
	}

	@Override
	public OWLDataRange visit(ElkDataUnionOf dr) {
		OWLDataRange[] ranges = new OWLDataRange[dr.getDataRanges().size()];
		
		for (int i = 0; i < ranges.length; i++) {
			ranges[i] = dr.getDataRanges().get(i).accept(this);
		}
		
		return factory_.getOWLDataUnionOf(ranges);
	}

	@Override
	public OWLLiteral visit(ElkLiteral elkLiteral) {
		return factory_.getOWLLiteral(elkLiteral.getLexicalForm(), getDatatype(elkLiteral.getDatatype()));
	}

	private OWLDatatype getDatatype(ElkDatatype dt) {
		return factory_.getOWLDatatype(IRI.create(dt.getIri().getFullIriAsString()));
	}
}
