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
package org.semanticweb.elk.owl.util;

import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.BaseElkLiteralVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Visitor for {@link ElkObject} that returns the standard OWL2 name of each
 * constructor.
 * 
 * @author Frantisek Simancik
 * 
 */
public class OwlObjectNameVisitor extends BaseElkLiteralVisitor<String> implements ElkObjectVisitor<String> {

	private static OwlObjectNameVisitor _INSTANCE = new OwlObjectNameVisitor();

	/**
	 * Returns the standard OWL2 name of the given ELK object.
	 * 
	 * @param elkObject
	 *            the object for which to return the OWL2 name
	 * @return the name of the top object' constructor
	 */
	public static String getName(ElkObject elkObject) {
		return elkObject.accept(_INSTANCE);
	}

	/* Declaration Axioms */

	@Override
	public String visit(ElkDeclarationAxiom elkDeclarationAxiom) {
		return "Declaration";
	}

	/* Class Axioms */

	@Override
	public String visit(ElkDisjointClassesAxiom elkDisjointClasses) {
		return "DisjointClasses";
	}

	@Override
	public String visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		return "DisjointUnion";
	}

	@Override
	public String visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		return "EquivalentClasses";
	}

	@Override
	public String visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
		return "SubClassOf";
	}

	/* Object Property Axioms */

	@Override
	public String visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		return "AsymmetricObjectProperty";
	}

	@Override
	public String visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		return "DisjointObjectProperties";
	}

	@Override
	public String visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		return "EquivalentObjectProperties";
	}

	@Override
	public String visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		return "FunctionalObjectProperty";
	}

	@Override
	public String visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		return "InverseFunctionalObjectProperty";
	}

	@Override
	public String visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		return "InverseObjectProperties";
	}

	@Override
	public String visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		return "IrreflexiveObjectProperty";
	}

	@Override
	public String visit(
			ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
		return "ObjectPropertyDomain";
	}

	@Override
	public String visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		return "ObjectPropertyRange";
	}

	@Override
	public String visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		return "ReflexiveObjectProperty";
	}

	@Override
	public String visit(ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		return "SubObjectPropertyOf";
	}

	@Override
	public String visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		return "SymmetricObjectProperty";
	}

	@Override
	public String visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		return "TransitiveObjectProperty";
	}

	/* Data Property Axioms */

	@Override
	public String visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		return "DataPropertyDomain";
	}

	@Override
	public String visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		return "DataPropertyRange";
	}

	@Override
	public String visit(
			ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		return "DisjointDataProperties";
	}

	@Override
	public String visit(
			ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		return "EquivalentDataProperties";
	}

	@Override
	public String visit(
			ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		return "FunctionalDataProperty";
	}

	@Override
	public String visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		return "SubDataPropertyOf";
	}

	/* Datatype Definition Axioms */

	@Override
	public String visit(ElkDatatypeDefinitionAxiom datatypeDefn) {
		return "DatatypeDefinition";
	}

	/* Key Axioms */

	@Override
	public String visit(ElkHasKeyAxiom elkHasKey) {
		return "HasKey";
	}

	/* Assertion Axioms */

	@Override
	public String visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
		return "ClassAssertion";
	}

	@Override
	public String visit(
			ElkDataPropertyAssertionAxiom elkDataPropertyAssertionAxiom) {
		return "DataPropertyAssertion";
	}

	@Override
	public String visit(
			ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		return "DifferentIndividuals";
	}

	@Override
	public String visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		return "NegativeDataPropertyAssertion";
	}

	@Override
	public String visit(
			ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion) {
		return "NegativeObjectPropertyAssertion";
	}

	@Override
	public String visit(
			ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom) {
		return "ObjectPropertyAssertion";
	}

	@Override
	public String visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		return "SameIndividual";
	}

	/* Annotation Axioms */

	@Override
	public String visit(
			ElkSubAnnotationPropertyOfAxiom subAnnotationPropertyOfAxiom) {
		return "SubAnnotationPropertyOf";
	}

	@Override
	public String visit(
			ElkAnnotationPropertyDomainAxiom annotationPropertyDomainAxiom) {
		return "AnnotationPropertyDomain";
	}

	@Override
	public String visit(
			ElkAnnotationPropertyRangeAxiom annotationPropertyRangeAxiom) {
		return "AnnotationPropertyRange";
	}

	@Override
	public String visit(ElkAnnotationAssertionAxiom elkAnnotationAssertionAxiom) {
		return "AnnotationAssertion";
	}

	/* Class Expressions */

	@Override
	public String visit(ElkClass elkClass) {
		return "Class";
	}

	@Override
	public String visit(ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		return "ObjectAllValuesFrom";
	}

	@Override
	public String visit(ElkObjectComplementOf elkObjectComplementOf) {
		return "ObjectComplementOf";
	}

	@Override
	public String visit(ElkObjectExactCardinality elkObjectExactCardinality) {
		return "ObjectExactCardinality";
	}

	@Override
	public String visit(
			ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified) {
		return "ObjectExactCardinality";
	}

	@Override
	public String visit(ElkObjectHasSelf elkObjectHasSelf) {
		return "ObjectHasSelf";
	}

	@Override
	public String visit(ElkObjectHasValue elkObjectHasValue) {
		return "ObjectHasValue";
	}

	@Override
	public String visit(ElkObjectIntersectionOf elkObjectIntersectionOf) {
		return "ObjectIntersectionOf";
	}

	@Override
	public String visit(ElkObjectMaxCardinality elkObjectMaxCardinality) {
		return "ObjectMaxCardinality";
	}

	@Override
	public String visit(
			ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified) {
		return "ObjectMaxCardinality";
	}

	@Override
	public String visit(ElkObjectMinCardinality elkObjectMinCardinality) {
		return "ObjectMinCardinality";
	}

	@Override
	public String visit(
			ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified) {
		return "ObjectMinCardinality";
	}

	@Override
	public String visit(ElkObjectOneOf elkObjectOneOf) {
		return "ObjectOneOf";
	}

	@Override
	public String visit(ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return "ObjectSomeValuesFrom";
	}

	@Override
	public String visit(ElkObjectUnionOf elkObjectUnionOf) {
		return "ObjectUnionOf";
	}

	@Override
	public String visit(ElkDataHasValue elkDataHasValue) {
		return "DataHasValue";
	}

	@Override
	public String visit(ElkDataMaxCardinality elkDataMaxCardinality) {
		return "DataMaxCardinality";
	}

	@Override
	public String visit(
			ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified) {
		return "DataMaxCardinality ";
	}

	@Override
	public String visit(ElkDataMinCardinality elkDataMinCardinality) {
		return "DataMinCardinality";
	}

	@Override
	public String visit(
			ElkDataMinCardinalityQualified elkDataMinCardinalityQualified) {
		return "DataMinCardinality";
	}

	@Override
	public String visit(ElkDataExactCardinality elkDataExactCardinality) {
		return "DataMinCardinality ";
	}

	@Override
	public String visit(
			ElkDataExactCardinalityQualified elkDataExactCardinalityQualified) {
		return "DataMinCardinality";
	}

	@Override
	public String visit(ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		return "DataSomeValuesFrom";
	}

	@Override
	public String visit(ElkDataAllValuesFrom elkDataAllValuesFrom) {
		return "DataAllValues";
	}

	/* Object Property Expressions */

	@Override
	public String visit(ElkObjectProperty elkObjectProperty) {
		return "ObjectProperty";
	}

	@Override
	public String visit(ElkObjectInverseOf elkObjectInverseOf) {
		return "ObjectInverseOf";
	}

	@Override
	public String visit(ElkObjectPropertyChain elkObjectPropertyChain) {
		return "ObjectPropertyChain";
	}

	/* Data Property Expressions */

	@Override
	public String visit(ElkDataProperty elkDataProperty) {
		return "DataProperty";
	}

	/* Individuals */

	@Override
	public String visit(ElkAnonymousIndividual elkAnonymousIndividual) {
		return "AnonymousIndividual";
	}

	@Override
	public String visit(ElkNamedIndividual elkNamedIndividual) {
		return "NamedIndividual";
	}

	/* Data Ranges */

	@Override
	public String visit(ElkDataComplementOf elkDataComplementOf) {
		return "DataComplementOf";
	}

	@Override
	public String visit(ElkDataIntersectionOf elkDataIntersectionOf) {
		return "DataIntersectionOf";
	}

	@Override
	public String visit(ElkDataOneOf elkDataOneOf) {
		return "DataOneOf";
	}

	@Override
	public String visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		return "DatatypeRestriction";
	}

	@Override
	public String visit(ElkDataUnionOf elkDataUnionOf) {
		return "DataUnionOf";
	}

	/* Other */

	@Override
	public String visit(ElkAnnotation elkAnnotation) {
		return "Annotation";
	}

	@Override
	public String visit(ElkAnnotationProperty elkAnnotationProperty) {
		return "AnnotationProperty";
	}

	@Override
	public String visit(ElkIri iri) {
		return "IRI";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.owl.visitors.ElkFacetRestrictionVisitor#visit(org
	 * .semanticweb.elk.owl.interfaces.ElkFacetRestriction)
	 * 
	 * This object does not exist in the standard OWL2 syntax.
	 */
	@Override
	public String visit(ElkFacetRestriction elkFacetRestriction) {
		return "FacetRestriction";
	}

	@Override
	public String visit(ElkDatatype elkDatatype) {
		return "Datatype";
	}

	@Override
	public String visit(ElkSWRLRule rule) {
		return "DLSafeRule";
	}

	@Override
	protected String defaultVisit(ElkLiteral elkLiteral) {
		return "Literal";
	}

}
