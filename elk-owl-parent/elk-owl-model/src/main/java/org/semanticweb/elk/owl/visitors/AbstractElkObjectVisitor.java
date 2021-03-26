/**
 * 
 */
package org.semanticweb.elk.owl.visitors;
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
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
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
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
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
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;

/**
 * A skeleton implementation
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 * 
 * @param <O>
 *            the type of the output of this visitor
 */
public abstract class AbstractElkObjectVisitor<O> implements ElkObjectVisitor<O> {

	protected abstract O defaultVisit(ElkObject obj);
	
	@Override
	public O visit(ElkDeclarationAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDisjointClassesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDisjointUnionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkSubClassOfAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkAsymmetricObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkDisjointObjectPropertiesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkEquivalentObjectPropertiesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkFunctionalObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkInverseFunctionalObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkInverseObjectPropertiesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkIrreflexiveObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkReflexiveObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkSymmetricObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkTransitiveObjectPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataPropertyDomainAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataPropertyRangeAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDisjointDataPropertiesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkEquivalentDataPropertiesAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkFunctionalDataPropertyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkSubDataPropertyOfAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDatatypeDefinitionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkHasKeyAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkClassAssertionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataPropertyAssertionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkNegativeDataPropertyAssertionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkNegativeObjectPropertyAssertionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkObjectPropertyAssertionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkSameIndividualAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkSubAnnotationPropertyOfAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkAnnotationPropertyDomainAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkAnnotationPropertyRangeAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkAnnotationAssertionAxiom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkSWRLRule obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkClass obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataAllValuesFrom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataExactCardinalityUnqualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkDataExactCardinalityQualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataHasValue obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataMaxCardinalityUnqualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataMaxCardinalityQualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataMinCardinalityUnqualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataMinCardinalityQualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataSomeValuesFrom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectAllValuesFrom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectComplementOf obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectExactCardinalityUnqualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkObjectExactCardinalityQualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectHasSelf obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectHasValue obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectIntersectionOf obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectMaxCardinalityUnqualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkObjectMaxCardinalityQualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectMinCardinalityUnqualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(
			ElkObjectMinCardinalityQualified obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectOneOf obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectSomeValuesFrom obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectUnionOf obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectPropertyChain obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectInverseOf obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkObjectProperty obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkDataProperty obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkAnonymousIndividual obj) {
		
		return defaultVisit(obj);
	}

	@Override
	public O visit(ElkNamedIndividual elkNamedIndividual) {
		
		return defaultVisit(elkNamedIndividual);
	}

	@Override
	public O visit(ElkLiteral elkLiteral) {
		
		return defaultVisit(elkLiteral);
	}

	@Override
	public O visit(ElkAnnotationProperty elkAnnotationProperty) {
		
		return defaultVisit(elkAnnotationProperty);
	}

	@Override
	public O visit(ElkDatatype elkDatatype) {
		
		return defaultVisit(elkDatatype);
	}

	@Override
	public O visit(ElkDataComplementOf elkDataComplementOf) {
		
		return defaultVisit(elkDataComplementOf);
	}

	@Override
	public O visit(ElkDataIntersectionOf elkDataIntersectionOf) {
		
		return defaultVisit(elkDataIntersectionOf);
	}

	@Override
	public O visit(ElkDataOneOf elkDataOneOf) {
		
		return defaultVisit(elkDataOneOf);
	}

	@Override
	public O visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		
		return defaultVisit(elkDatatypeRestriction);
	}

	@Override
	public O visit(ElkDataUnionOf elkDataUnionOf) {
		
		return defaultVisit(elkDataUnionOf);
	}

	@Override
	public O visit(ElkFacetRestriction restricition) {
		
		return defaultVisit(restricition);
	}

	@Override
	public O visit(ElkAnnotation elkAnnotation) {
		
		return defaultVisit(elkAnnotation);
	}

	@Override
	public O visit(ElkFullIri iri) {
		
		return defaultVisit(iri);
	}
	
	@Override
	public O visit(ElkAbbreviatedIri iri) {
		
		return defaultVisit(iri);
	}

}
