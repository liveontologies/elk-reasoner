package org.semanticweb.elk.owl.comparison;

/*
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.Arrays;

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
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

// TODO: move to some other module, e.g., elk-owl-util
public class ElkObjectSyntacticHasherVisitor implements
		ElkObjectVisitor<Integer> {

	static int combinedHashCode(Object... objects) {
		return Arrays.deepHashCode(objects);
	}

	@Override
	public Integer visit(ElkAnnotationAssertionAxiom axiom) {
		return combinedHashCode(ElkAnnotationAssertionAxiom.class,
				axiom.getSubject(), axiom.getProperty(), axiom.getValue());
	}

	@Override
	public Integer visit(ElkAnnotationPropertyDomainAxiom axiom) {
		return combinedHashCode(ElkAnnotationPropertyDomainAxiom.class,
				axiom.getProperty(), axiom.getDomain());
	}

	@Override
	public Integer visit(ElkAnnotationPropertyRangeAxiom axiom) {
		return combinedHashCode(ElkAnnotationPropertyRangeAxiom.class,
				axiom.getProperty(), axiom.getRange());
	}

	@Override
	public Integer visit(ElkSubAnnotationPropertyOfAxiom axiom) {
		return combinedHashCode(ElkSubAnnotationPropertyOfAxiom.class,
				axiom.getSubAnnotationProperty(),
				axiom.getSuperAnnotationProperty());
	}

	@Override
	public Integer visit(ElkClassAssertionAxiom axiom) {
		return combinedHashCode(ElkClassAssertionAxiom.class,
				axiom.getClassExpression(), axiom.getIndividual());
	}

	@Override
	public Integer visit(ElkDifferentIndividualsAxiom axiom) {
		return combinedHashCode(ElkDifferentIndividualsAxiom.class,
				axiom.getIndividuals());
	}

	@Override
	public Integer visit(ElkDataPropertyAssertionAxiom axiom) {
		return combinedHashCode(ElkDataPropertyAssertionAxiom.class,
				axiom.getProperty(), axiom.getObject(), axiom.getSubject());
	}

	@Override
	public Integer visit(ElkNegativeDataPropertyAssertionAxiom axiom) {
		return combinedHashCode(ElkNegativeDataPropertyAssertionAxiom.class,
				axiom.getProperty(), axiom.getObject(), axiom.getSubject());
	}

	@Override
	public Integer visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		return combinedHashCode(ElkNegativeObjectPropertyAssertionAxiom.class,
				axiom.getProperty(), axiom.getObject(), axiom.getSubject());
	}

	@Override
	public Integer visit(ElkObjectPropertyAssertionAxiom axiom) {
		return combinedHashCode(ElkObjectPropertyAssertionAxiom.class,
				axiom.getProperty(), axiom.getObject(), axiom.getSubject());
	}

	@Override
	public Integer visit(ElkSameIndividualAxiom axiom) {
		return combinedHashCode(ElkSameIndividualAxiom.class,
				axiom.getIndividuals());
	}

	@Override
	public Integer visit(ElkDisjointClassesAxiom axiom) {
		return combinedHashCode(ElkDisjointClassesAxiom.class,
				axiom.getClassExpressions());
	}

	@Override
	public Integer visit(ElkDisjointUnionAxiom axiom) {
		return combinedHashCode(ElkDisjointUnionAxiom.class,
				axiom.getDefinedClass(), axiom.getClassExpressions());
	}

	@Override
	public Integer visit(ElkEquivalentClassesAxiom axiom) {
		return combinedHashCode(ElkEquivalentClassesAxiom.class,
				axiom.getClassExpressions());
	}

	@Override
	public Integer visit(ElkSubClassOfAxiom axiom) {
		return combinedHashCode(ElkSubClassOfAxiom.class,
				axiom.getSubClassExpression(), axiom.getSuperClassExpression());
	}

	@Override
	public Integer visit(ElkDataPropertyDomainAxiom axiom) {
		return combinedHashCode(ElkDataPropertyDomainAxiom.class,
				axiom.getProperty(), axiom.getDomain());
	}

	@Override
	public Integer visit(ElkDataPropertyRangeAxiom axiom) {
		return combinedHashCode(ElkDataPropertyRangeAxiom.class,
				axiom.getProperty(), axiom.getRange());
	}

	@Override
	public Integer visit(ElkDisjointDataPropertiesAxiom axiom) {
		return combinedHashCode(ElkDisjointDataPropertiesAxiom.class,
				axiom.getDataPropertyExpressions());
	}

	@Override
	public Integer visit(ElkEquivalentDataPropertiesAxiom axiom) {
		return combinedHashCode(ElkEquivalentDataPropertiesAxiom.class,
				axiom.getDataPropertyExpressions());
	}

	@Override
	public Integer visit(ElkFunctionalDataPropertyAxiom axiom) {
		return combinedHashCode(ElkFunctionalDataPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkSubDataPropertyOfAxiom axiom) {
		return combinedHashCode(ElkSubDataPropertyOfAxiom.class,
				axiom.getSubDataPropertyExpression(),
				axiom.getSuperDataPropertyExpression());
	}

	@Override
	public Integer visit(ElkDatatypeDefinitionAxiom axiom) {
		return combinedHashCode(ElkDatatypeDefinitionAxiom.class,
				axiom.getDatatype(), axiom.getDataRange());
	}

	@Override
	public Integer visit(ElkDeclarationAxiom axiom) {
		return combinedHashCode(ElkDeclarationAxiom.class, axiom.getEntity());
	}

	@Override
	public Integer visit(ElkHasKeyAxiom axiom) {
		return combinedHashCode(ElkHasKeyAxiom.class,
				axiom.getClassExpression(),
				axiom.getObjectPropertyExpressions(),
				axiom.getDataPropertyExpressions());
	}

	@Override
	public Integer visit(ElkAsymmetricObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkAsymmetricObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkDisjointObjectPropertiesAxiom axiom) {
		return combinedHashCode(ElkDisjointObjectPropertiesAxiom.class,
				axiom.getObjectPropertyExpressions());
	}

	@Override
	public Integer visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		return combinedHashCode(ElkEquivalentObjectPropertiesAxiom.class,
				axiom.getObjectPropertyExpressions());
	}

	@Override
	public Integer visit(ElkFunctionalObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkFunctionalObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkInverseFunctionalObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkInverseObjectPropertiesAxiom axiom) {
		return combinedHashCode(ElkInverseObjectPropertiesAxiom.class,
				axiom.getFirstObjectPropertyExpression(),
				axiom.getSecondObjectPropertyExpression());
	}

	@Override
	public Integer visit(ElkIrreflexiveObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkIrreflexiveObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkObjectPropertyDomainAxiom axiom) {
		return combinedHashCode(ElkObjectPropertyDomainAxiom.class,
				axiom.getProperty(), axiom.getDomain());
	}

	@Override
	public Integer visit(ElkObjectPropertyRangeAxiom axiom) {
		return combinedHashCode(ElkObjectPropertyRangeAxiom.class,
				axiom.getProperty(), axiom.getRange());
	}

	@Override
	public Integer visit(ElkReflexiveObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkReflexiveObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkSubObjectPropertyOfAxiom axiom) {
		return combinedHashCode(ElkSubObjectPropertyOfAxiom.class,
				axiom.getSubObjectPropertyExpression(),
				axiom.getSuperObjectPropertyExpression());
	}

	@Override
	public Integer visit(ElkSymmetricObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkSymmetricObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkTransitiveObjectPropertyAxiom axiom) {
		return combinedHashCode(ElkTransitiveObjectPropertyAxiom.class,
				axiom.getProperty());
	}

	@Override
	public Integer visit(ElkSWRLRule axiom) {
		return combinedHashCode(ElkSWRLRule.class);
	}

	@Override
	public Integer visit(ElkClass expression) {
		return combinedHashCode(ElkClass.class, expression.getIri());
	}

	@Override
	public Integer visit(ElkDataAllValuesFrom expression) {
		return combinedHashCode(ElkDataAllValuesFrom.class,
				expression.getDataPropertyExpressions(),
				expression.getDataRange());
	}

	@Override
	public Integer visit(ElkDataSomeValuesFrom expression) {
		return combinedHashCode(ElkDataSomeValuesFrom.class,
				expression.getDataPropertyExpressions(),
				expression.getDataRange());
	}

	@Override
	public Integer visit(ElkObjectComplementOf expression) {
		return combinedHashCode(ElkObjectComplementOf.class,
				expression.getClassExpression());
	}

	@Override
	public Integer visit(ElkObjectIntersectionOf expression) {
		return combinedHashCode(ElkObjectSomeValuesFrom.class,
				expression.getClassExpressions());
	}

	@Override
	public Integer visit(ElkObjectOneOf expression) {
		return combinedHashCode(ElkObjectOneOf.class,
				expression.getIndividuals());
	}

	@Override
	public Integer visit(ElkObjectUnionOf expression) {
		return combinedHashCode(ElkObjectUnionOf.class,
				expression.getClassExpressions());
	}

	@Override
	public Integer visit(ElkDataExactCardinalityQualified expression) {
		return combinedHashCode(ElkDataExactCardinalityQualified.class,
				expression.getProperty(), expression.getCardinality(),
				expression.getFiller());
	}

	@Override
	public Integer visit(ElkDataMaxCardinalityQualified expression) {
		return combinedHashCode(ElkDataMaxCardinalityQualified.class,
				expression.getProperty(), expression.getCardinality(),
				expression.getFiller());
	}

	@Override
	public Integer visit(ElkDataMinCardinalityQualified expression) {
		return combinedHashCode(ElkDataMinCardinalityQualified.class,
				expression.getProperty(), expression.getCardinality(),
				expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectExactCardinalityQualified expression) {
		return combinedHashCode(ElkObjectExactCardinalityQualified.class,
				expression.getProperty(), expression.getCardinality(),
				expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectMaxCardinalityQualified expression) {
		return combinedHashCode(ElkObjectMaxCardinalityQualified.class,
				expression.getProperty(), expression.getCardinality(),
				expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectMinCardinalityQualified expression) {
		return combinedHashCode(ElkObjectMinCardinalityQualified.class,
				expression.getProperty(), expression.getCardinality(),
				expression.getFiller());
	}

	@Override
	public Integer visit(ElkDataExactCardinalityUnqualified expression) {
		return combinedHashCode(ElkDataExactCardinalityUnqualified.class,
				expression.getProperty(), expression.getCardinality());
	}

	@Override
	public Integer visit(ElkDataMaxCardinalityUnqualified expression) {
		return combinedHashCode(ElkDataMaxCardinalityUnqualified.class,
				expression.getProperty(), expression.getCardinality());
	}

	@Override
	public Integer visit(ElkDataMinCardinalityUnqualified expression) {
		return combinedHashCode(ElkDataMinCardinalityUnqualified.class,
				expression.getProperty(), expression.getCardinality());
	}

	@Override
	public Integer visit(ElkObjectExactCardinalityUnqualified expression) {
		return combinedHashCode(ElkObjectExactCardinalityUnqualified.class,
				expression.getProperty(), expression.getCardinality());
	}

	@Override
	public Integer visit(ElkObjectMaxCardinalityUnqualified expression) {
		return combinedHashCode(ElkObjectMaxCardinalityUnqualified.class,
				expression.getProperty(), expression.getCardinality());
	}

	@Override
	public Integer visit(ElkObjectMinCardinalityUnqualified expression) {
		return combinedHashCode(ElkObjectMinCardinalityUnqualified.class,
				expression.getProperty(), expression.getCardinality());
	}

	@Override
	public Integer visit(ElkObjectHasSelf expression) {
		return combinedHashCode(ElkObjectHasSelf.class,
				expression.getProperty());
	}

	@Override
	public Integer visit(ElkDataHasValue expression) {
		return combinedHashCode(ElkDataHasValue.class,
				expression.getProperty(), expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectAllValuesFrom expression) {
		return combinedHashCode(ElkObjectAllValuesFrom.class,
				expression.getProperty(), expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectHasValue expression) {
		return combinedHashCode(ElkObjectHasValue.class,
				expression.getProperty(), expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectSomeValuesFrom expression) {
		return combinedHashCode(ElkObjectSomeValuesFrom.class,
				expression.getProperty(), expression.getFiller());
	}

	@Override
	public Integer visit(ElkObjectPropertyChain expression) {
		return combinedHashCode(ElkObjectPropertyChain.class,
				expression.getObjectPropertyExpressions());
	}

	@Override
	public Integer visit(ElkObjectInverseOf expression) {
		return combinedHashCode(ElkObjectInverseOf.class,
				expression.getObjectProperty());
	}

	@Override
	public Integer visit(ElkObjectProperty expression) {
		return combinedHashCode(ElkObjectProperty.class, expression.getIri());
	}

	@Override
	public Integer visit(ElkDataProperty expression) {
		return combinedHashCode(ElkDataProperty.class, expression.getIri());
	}

	@Override
	public Integer visit(ElkAnonymousIndividual expression) {
		return combinedHashCode(ElkAnonymousIndividual.class,
				expression.getNodeId());
	}

	@Override
	public Integer visit(ElkNamedIndividual expression) {
		return combinedHashCode(ElkNamedIndividual.class, expression.getIri());
	}

	@Override
	public Integer visit(ElkLiteral expression) {
		return combinedHashCode(ElkLiteral.class, expression.getLexicalForm(),
				expression.getDatatype());
	}

	@Override
	public Integer visit(ElkAnnotationProperty expression) {
		return combinedHashCode(ElkAnnotationProperty.class,
				expression.getIri());
	}

	@Override
	public Integer visit(ElkDatatype expression) {
		return combinedHashCode(ElkDatatype.class, expression.getIri());
	}

	@Override
	public Integer visit(ElkDataComplementOf expression) {
		return combinedHashCode(ElkDataComplementOf.class,
				expression.getDataRange());
	}

	@Override
	public Integer visit(ElkDataIntersectionOf expression) {
		return combinedHashCode(ElkDataIntersectionOf.class,
				expression.getDataRanges());
	}

	@Override
	public Integer visit(ElkDataOneOf expression) {
		return combinedHashCode(ElkDataOneOf.class, expression.getLiterals());
	}

	@Override
	public Integer visit(ElkDatatypeRestriction expression) {
		return combinedHashCode(ElkDatatypeRestriction.class,
				expression.getDatatype(), expression.getFacetRestrictions());
	}

	@Override
	public Integer visit(ElkDataUnionOf expression) {
		return combinedHashCode(ElkDataUnionOf.class,
				expression.getDataRanges());
	}

	@Override
	public Integer visit(ElkFacetRestriction expression) {
		return combinedHashCode(ElkFacetRestriction.class,
				expression.getConstrainingFacet(),
				expression.getRestrictionValue());
	}

	@Override
	public Integer visit(ElkAnnotation expression) {
		return combinedHashCode(ElkAnnotation.class, expression.getProperty(),
				expression.getValue());
	}

	@Override
	public Integer visit(ElkFullIri expression) {
		return combinedHashCode(ElkFullIri.class,
				expression.getFullIriAsString());
	}

	@Override
	public Integer visit(ElkAbbreviatedIri expression) {
		return combinedHashCode(ElkAbbreviatedIri.class,
				expression.getPrefix(), expression.getLocalName());
	}

}
