/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Datatype_Maps">built-in datatypes<a> in
 * the OWL 2 specification.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public enum PredefinedElkDatatype implements ElkDatatype {

	OWL_REAL(PredefinedElkIris.OWL_REAL),

	OWL_RATIONAL(PredefinedElkIris.OWL_RATIONAL),

	XSD_DECIMAL(PredefinedElkIris.XSD_DECIMAL),

	XSD_INTEGER(PredefinedElkIris.XSD_INTEGER),

	XSD_NON_NEGATIVE_INTEGER(PredefinedElkIris.XSD_NON_NEGATIVE_INTEGER),

	XSD_NON_POSITIVE_INTEGER(PredefinedElkIris.XSD_NON_POSITIVE_INTEGER),

	XSD_POSITIVE_INTEGER(PredefinedElkIris.XSD_POSITIVE_INTEGER),

	XSD_NEGATIVE_INTEGER(PredefinedElkIris.XSD_NEGATIVE_INTEGER),

	XSD_LONG(PredefinedElkIris.XSD_LONG),

	XSD_INT(PredefinedElkIris.XSD_INT),

	XSD_SHORT(PredefinedElkIris.XSD_SHORT),

	XSD_BYTE(PredefinedElkIris.XSD_BYTE),

	XSD_UNSIGNED_LONG(PredefinedElkIris.XSD_UNSIGNED_LONG),

	XSD_UNSIGNED_INT(PredefinedElkIris.XSD_UNSIGNED_INT),

	XSD_UNSIGNED_SHORT(PredefinedElkIris.XSD_UNSIGNED_SHORT),

	XSD_UNSIGNED_BYTE(PredefinedElkIris.XSD_UNSIGNED_BYTE),

	XSD_DOUBLE(PredefinedElkIris.XSD_DOUBLE),

	XSD_FLOAT(PredefinedElkIris.XSD_FLOAT),

	XSD_STRING(PredefinedElkIris.XSD_STRING),

	XSD_NORMALIZED_STRING(PredefinedElkIris.XSD_NORMALIZED_STRING),

	XSD_TOKEN(PredefinedElkIris.XSD_TOKEN),

	XSD_LANGUAGE(PredefinedElkIris.XSD_LANGUAGE),

	XSD_NAME(PredefinedElkIris.XSD_NAME),

	XSD_NC_NAME(PredefinedElkIris.XSD_NC_NAME),

	XSD_NM_TOKEN(PredefinedElkIris.XSD_NM_TOKEN),

	XSD_HEX_BINARY(PredefinedElkIris.XSD_HEX_BINARY),

	XSD_BASE_64_BINARY(PredefinedElkIris.XSD_BASE_64_BINARY),

	XSD_ANY_URI(PredefinedElkIris.XSD_ANY_URI),

	XSD_DATE_TIME(PredefinedElkIris.XSD_DATE_TIME),

	XSD_DATE_TIME_STAMP(PredefinedElkIris.XSD_DATE_TIME_STAMP),

	RDF_XML_LITERAL(PredefinedElkIris.RDF_XML_LITERAL),

	;

	private final ElkIri iri_;

	private PredefinedElkDatatype(ElkIri iri) {
		this.iri_ = iri;
	}

	@Override
	public ElkIri getIri() {
		return iri_;
	}

	@Override
	public ElkEntityType getEntityType() {
		return ElkEntityType.DATATYPE;
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return accept((ElkDatatypeVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDatatypeVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDatatypeVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return accept((ElkDatatypeVisitor<O>) visitor);
	}

}
