/*
 * #%L
 * ELK OWL Model Implementation
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
/**
 * 
 */
package org.semanticweb.elk.owl;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

/**
 * Does nothing, simply helps subclasses to focus on methods they want to
 * implement
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractElkAxiomVisitor<O> implements ElkAxiomVisitor<O> {

	/**
	 * Invoked to visit every logical axiom
	 */
	protected O defaultLogicalVisit(ElkAxiom axiom) {
		return null;
	}
	
	/**
	 * Invoked to visit every non-logical (annotation) axiom
	 */
	protected O defaultNonLogicalVisit(ElkAxiom axiom) {
		return null;
	}

	@Override
	public O visit(ElkDisjointClassesAxiom elkDisjointClasses) {
		return defaultLogicalVisit(elkDisjointClasses);
	}

	@Override
	public O visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		return defaultLogicalVisit(elkDisjointUnionAxiom);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		return defaultLogicalVisit(elkEquivalentClassesAxiom);
	}

	@Override
	public O visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
		return defaultLogicalVisit(elkSubClassOfAxiom);
	}

	@Override
	public O visit(ElkDatatypeDefinitionAxiom datatypeDefn) {
		return defaultLogicalVisit(datatypeDefn);
	}

	@Override
	public O visit(ElkHasKeyAxiom elkHasKey) {
		return defaultLogicalVisit(elkHasKey);
	}

	@Override
	public O visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		return defaultLogicalVisit(elkAsymmetricObjectPropertyAxiom);
	}

	@Override
	public O visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		
		return defaultLogicalVisit(elkDisjointObjectPropertiesAxiom);
	}

	@Override
	public O visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		return defaultLogicalVisit(elkEquivalentObjectProperties);
	}

	@Override
	public O visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		return defaultLogicalVisit(elkFunctionalObjectPropertyAxiom);
	}

	@Override
	public O visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		return defaultLogicalVisit(elkInverseFunctionalObjectPropertyAxiom);
	}

	@Override
	public O visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		return defaultLogicalVisit(elkInverseObjectPropertiesAxiom);
	}

	@Override
	public O visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		return defaultLogicalVisit(elkIrreflexiveObjectPropertyAxiom);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
		return defaultLogicalVisit(elkObjectPropertyDomainAxiom);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		return defaultLogicalVisit(elkObjectPropertyRangeAxiom);
	}

	@Override
	public O visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		
		return defaultLogicalVisit(elkReflexiveObjectPropertyAxiom);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		
		return defaultLogicalVisit(elkSubObjectPropertyOfAxiom);
	}

	@Override
	public O visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		
		return defaultLogicalVisit(elkSymmetricObjectPropertyAxiom);
	}

	@Override
	public O visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		
		return defaultLogicalVisit(elkTransitiveObjectPropertyAxiom);
	}

	@Override
	public O visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		
		return defaultLogicalVisit(elkDataPropertyDomainAxiom);
	}

	@Override
	public O visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		
		return defaultLogicalVisit(elkDataPropertyRangeAxiom);
	}

	@Override
	public O visit(ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		
		return defaultLogicalVisit(elkDisjointDataPropertiesAxiom);
	}

	@Override
	public O visit(ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		
		return defaultLogicalVisit(elkEquivalentDataProperties);
	}

	@Override
	public O visit(ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		
		return defaultLogicalVisit(elkFunctionalDataPropertyAxiom);
	}

	@Override
	public O visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		
		return defaultLogicalVisit(elkSubDataPropertyOfAxiom);
	}

	@Override
	public O visit(ElkAnnotationAssertionAxiom elkAnnotationAssertionAxiom) {
		
		return defaultNonLogicalVisit(elkAnnotationAssertionAxiom);
	}

	@Override
	public O visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
		
		return defaultLogicalVisit(elkClassAssertionAxiom);
	}

	@Override
	public O visit(ElkDataPropertyAssertionAxiom elkDataPropertyAssertionAxiom) {
		
		return defaultLogicalVisit(elkDataPropertyAssertionAxiom);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		
		return defaultLogicalVisit(elkDifferentIndividualsAxiom);
	}

	@Override
	public O visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		
		return defaultLogicalVisit(elkNegativeDataPropertyAssertion);
	}

	@Override
	public O visit(
			ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion) {
		
		return defaultLogicalVisit(elkNegativeObjectPropertyAssertion);
	}

	@Override
	public O visit(
			ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom) {
		
		return defaultLogicalVisit(elkObjectPropertyAssertionAxiom);
	}

	@Override
	public O visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		
		return defaultLogicalVisit(elkSameIndividualAxiom);
	}

	@Override
	public O visit(ElkSubAnnotationPropertyOfAxiom subAnnotationPropertyOfAxiom) {
		
		return defaultNonLogicalVisit(subAnnotationPropertyOfAxiom);
	}

	@Override
	public O visit(
			ElkAnnotationPropertyDomainAxiom annotationPropertyDomainAxiom) {
		
		return defaultNonLogicalVisit(annotationPropertyDomainAxiom);
	}

	@Override
	public O visit(ElkAnnotationPropertyRangeAxiom annotationPropertyRangeAxiom) {
		
		return defaultNonLogicalVisit(annotationPropertyRangeAxiom);
	}

	@Override
	public O visit(ElkDeclarationAxiom elkDeclarationAxiom) {
		
		return defaultLogicalVisit(elkDeclarationAxiom);
	}

}
