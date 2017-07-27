package org.semanticweb.elk.owlapi.wrapper;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;

/**
 * An implementation of the visitor pattern for OWL data ranges to convert OWL
 * data ranges to ELK data ranges.
 * 
 * @author "Yevgeny Kazakov"
 */
public class OwlDataRangeConverterVisitor
		implements OWLDataRangeVisitorEx<ElkDataRange> {

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	private static OwlDataRangeConverterVisitor INSTANCE_ = new OwlDataRangeConverterVisitor();

	public static OwlDataRangeConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlDataRangeConverterVisitor() {
	}

	@Override
	public ElkDataComplementOf visit(OWLDataComplementOf owlDataComplementOf) {
		return CONVERTER.convert(owlDataComplementOf);
	}

	@Override
	public ElkDataIntersectionOf visit(
			OWLDataIntersectionOf owlDataIntersectionOf) {
		return CONVERTER.convert(owlDataIntersectionOf);
	}

	@Override
	public ElkDataOneOf visit(OWLDataOneOf owlDataOneOf) {
		return CONVERTER.convert(owlDataOneOf);
	}

	@Override
	public ElkDatatype visit(OWLDatatype owlDatatype) {
		return CONVERTER.convert(owlDatatype);
	}

	@Override
	public ElkDatatypeRestriction visit(
			OWLDatatypeRestriction owlDatatypeRestriction) {
		return CONVERTER.convert(owlDatatypeRestriction);
	}

	@Override
	public ElkDataUnionOf visit(OWLDataUnionOf owlDataUnionOf) {
		return CONVERTER.convert(owlDataUnionOf);
	}

}
