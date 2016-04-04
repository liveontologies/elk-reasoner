package org.semanticweb.elk.owl.visitors;

/*
 * #%L
 * ELK OWL Object Interfaces
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
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
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
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
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
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
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;

/**
 * An {@link ElkObjectVisitor} that always returns {@code null}. Can be used as
 * a prototype for other visitors by overriding the default visit methods.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 */
public class ElkObjectDummyVisitor<O> implements ElkObjectVisitor<O> {

	protected O defaultVisit(ElkObject object) {
		return null;
	}

	protected O defaultVisit(ElkAxiom axiom) {
		return defaultVisit((ElkObject) axiom);
	}

	protected O defaultVisit(ElkClassExpression expression) {
		return defaultVisit((ElkObject) expression);
	}

	protected O defaultVisit(ElkDataPropertyExpression expression) {
		return defaultVisit((ElkObject) expression);
	}

	protected O defaultVisit(ElkDataRange expression) {
		return defaultVisit((ElkObject) expression);
	}

	protected O defaultVisit(ElkIndividual expression) {
		return defaultVisit((ElkObject) expression);
	}

	protected O visit(ElkSubObjectPropertyExpression expression) {
		return defaultVisit((ElkObject) expression);
	}

	protected O defaultVisit(ElkAnnotationAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}
	
	protected O defaultVisit(ElkAssertionAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}
	
	protected O defaultVisit(ElkClassAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}
	
	protected O defaultVisit(ElkDataPropertyAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}
	
	protected O defaultVisit(ElkDeclarationAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}
	
	protected O defaultVisit(ElkHasKeyAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}
	
	protected O defaultVisit(ElkObjectPropertyAxiom axiom) {
		return defaultVisit((ElkAxiom) axiom);
	}	
	
	protected O visit(ElkObjectPropertyExpression expression) {
		return defaultVisit((ElkSubObjectPropertyExpression) expression);
	}

	@Override
	public O visit(ElkAnnotationAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkAnnotationPropertyDomainAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkAnnotationPropertyRangeAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSubAnnotationPropertyOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkClassAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkNegativeDataPropertyAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSameIndividualAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDisjointClassesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDisjointUnionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSubClassOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyDomainAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyRangeAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDisjointDataPropertiesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkEquivalentDataPropertiesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkFunctionalDataPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSubDataPropertyOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDatatypeDefinitionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDeclarationAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkHasKeyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkAsymmetricObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDisjointObjectPropertiesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkFunctionalObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkInverseObjectPropertiesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkIrreflexiveObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSymmetricObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSWRLRule axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkClass expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataAllValuesFrom expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataSomeValuesFrom expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectComplementOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectIntersectionOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectOneOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectUnionOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataExactCardinalityQualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataMaxCardinalityQualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataMinCardinalityQualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectExactCardinalityQualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectMaxCardinalityQualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectMinCardinalityQualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataExactCardinalityUnqualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataMaxCardinalityUnqualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataMinCardinalityUnqualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectExactCardinalityUnqualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectMaxCardinalityUnqualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectMinCardinalityUnqualified expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectHasSelf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataHasValue expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectAllValuesFrom expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectHasValue expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectSomeValuesFrom expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectPropertyChain expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectInverseOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkObjectProperty expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataProperty expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkAnonymousIndividual expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkNamedIndividual expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkLiteral expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkAnnotationProperty expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDatatype expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataComplementOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataIntersectionOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataOneOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDatatypeRestriction expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkDataUnionOf expression) {
		return defaultVisit(expression);
	}

	@Override
	public O visit(ElkFacetRestriction restricition) {
		return defaultVisit(restricition);
	}

	@Override
	public O visit(ElkAnnotation annotation) {
		return defaultVisit(annotation);
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
