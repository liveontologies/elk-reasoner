package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
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

import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDeclarationAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entity_Declarations_and_Typing"
 * >declarations of built-in entities<a> in the OWL 2 specification that are
 * implicitly present in every OWL 2 ontology (see Table 5 in the link).
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public enum PredefinedElkDeclaration implements ElkDeclarationAxiom {

	/* class declarations */

	OWL_THING_DECLARATION(PredefinedElkClass.OWL_THING),

	OWL_NOTHING_DECLARATION(PredefinedElkClass.OWL_NOTHING),

	/* object property declarations */

	OWL_TOP_OBJECT_PROPERTY_DECLARATION(
			PredefinedElkObjectProperty.OWL_TOP_OBJECT_PROPERTY),

	OWL_BOTTOM_OBJECT_PROPERTY_DECLARATION(
			PredefinedElkObjectProperty.OWL_BOTTOM_OBJECT_PROPERTY),

	/* data property declarations */

	OWL_TOP_DATA_PROPERTY_DECLARATION(
			PredefinedElkDataProperty.OWL_TOP_DATA_PROPERTY),

	OWL_BOTTOM_DATA_PROPERTY_DECLARATION(
			PredefinedElkDataProperty.OWL_BOTTOM_DATA_PROPERTY),

	RDFS_LITERAL_DECLARATION_DECLARATION(PredefinedElkDatatype.RDFS_LITERAL),

	/* datatype declarations */

	OWL_REAL_DECLARATION_DECLARATION(PredefinedElkDatatype.OWL_REAL),

	OWL_RATIONAL_DECLARATION_DECLARATION(PredefinedElkDatatype.OWL_RATIONAL),

	XSD_DECIMAL_DECLARATION(PredefinedElkDatatype.XSD_DECIMAL),

	XSD_INTEGER_DECLARATION(PredefinedElkDatatype.XSD_INTEGER),

	XSD_NON_NEGATIVE_INTEGER_DECLARATION(
			PredefinedElkDatatype.XSD_NON_NEGATIVE_INTEGER),

	XSD_NON_POSITIVE_INTEGER_DECLARATION(
			PredefinedElkDatatype.XSD_NON_POSITIVE_INTEGER),

	XSD_POSITIVE_INTEGER_DECLARATION(PredefinedElkDatatype.XSD_POSITIVE_INTEGER),

	XSD_NEGATIVE_INTEGER_DECLARATION(PredefinedElkDatatype.XSD_NEGATIVE_INTEGER),

	XSD_LONG_DECLARATION(PredefinedElkDatatype.XSD_LONG),

	XSD_INT_DECLARATION(PredefinedElkDatatype.XSD_INT),

	XSD_SHORT_DECLARATION(PredefinedElkDatatype.XSD_SHORT),

	XSD_BYTE_DECLARATION(PredefinedElkDatatype.XSD_BYTE),

	XSD_UNSIGNED_LONG_DECLARATION(PredefinedElkDatatype.XSD_UNSIGNED_LONG),

	XSD_UNSIGNED_INT_DECLARATION(PredefinedElkDatatype.XSD_UNSIGNED_INT),

	XSD_UNSIGNED_SHORT_DECLARATION(PredefinedElkDatatype.XSD_UNSIGNED_SHORT),

	XSD_UNSIGNED_BYTE_DECLARATION(PredefinedElkDatatype.XSD_UNSIGNED_BYTE),

	XSD_DOUBLE_DECLARATION(PredefinedElkDatatype.XSD_DOUBLE),

	XSD_FLOAT_DECLARATION(PredefinedElkDatatype.XSD_FLOAT),

	XSD_STRING_DECLARATION(PredefinedElkDatatype.XSD_STRING),

	XSD_NORMALIZED_STRING_DECLARATION(
			PredefinedElkDatatype.XSD_NORMALIZED_STRING),

	XSD_TOKEN_DECLARATION(PredefinedElkDatatype.XSD_TOKEN),

	XSD_LANGUAGE_DECLARATION(PredefinedElkDatatype.XSD_LANGUAGE),

	XSD_NAME_DECLARATION(PredefinedElkDatatype.XSD_NAME),

	XSD_NC_NAME_DECLARATION(PredefinedElkDatatype.XSD_NC_NAME),

	XSD_NM_TOKEN_DECLARATION(PredefinedElkDatatype.XSD_NM_TOKEN),

	XSD_HEX_BINARY_DECLARATION(PredefinedElkDatatype.XSD_HEX_BINARY),

	XSD_BASE_64_BINARY_DECLARATION(PredefinedElkDatatype.XSD_BASE_64_BINARY),

	XSD_ANY_URI_DECLARATION(PredefinedElkDatatype.XSD_ANY_URI),

	XSD_DATE_TIME_DECLARATION(PredefinedElkDatatype.XSD_DATE_TIME),

	XSD_DATE_TIME_STAMP_DECLARATION(PredefinedElkDatatype.XSD_DATE_TIME_STAMP),

	RDF_XML_LITERAL_DECLARATION(PredefinedElkDatatype.RDF_XML_LITERAL),

	/* annotation property declarations */

	;

	private final ElkEntity entity_;

	private PredefinedElkDeclaration(ElkEntity entity) {
		this.entity_ = entity;
	}

	@Override
	public ElkEntity getEntity() {
		return entity_;
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return accept((ElkDeclarationAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDeclarationAxiomVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDeclarationAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
