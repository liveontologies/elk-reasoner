/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
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
 * TODO docs
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DelegatingElkAxiomVisitor implements ElkAxiomVisitor<Void>{

	private final ElkAxiomVisitor<Void> visitor_;
	
	public DelegatingElkAxiomVisitor(ElkAxiomVisitor<Void> visitor) {
		this.visitor_ = visitor;
	}
	
	protected ElkAxiomVisitor<Void> getVisitor() {
		return visitor_;
	}

	@Override
	public Void visit(ElkDeclarationAxiom elkDeclarationAxiom) {
		return elkDeclarationAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkDisjointClassesAxiom elkDisjointClasses) {
		return elkDisjointClasses.accept(visitor_);
	}

	@Override
	public Void visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		return elkDisjointUnionAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		return elkEquivalentClassesAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
		return elkSubClassOfAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		return elkAsymmetricObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		return elkDisjointObjectPropertiesAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		return elkEquivalentObjectProperties.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		return elkFunctionalObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		return elkInverseFunctionalObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		return elkInverseObjectPropertiesAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		return elkIrreflexiveObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
		return elkObjectPropertyDomainAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		return elkObjectPropertyRangeAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		return elkReflexiveObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		return elkSubObjectPropertyOfAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		return elkSymmetricObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		return elkTransitiveObjectPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		return elkDataPropertyDomainAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		return elkDataPropertyRangeAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		return elkDisjointDataPropertiesAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		return elkEquivalentDataProperties.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		return elkFunctionalDataPropertyAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		return elkSubDataPropertyOfAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkDatatypeDefinitionAxiom datatypeDefn) {
		return datatypeDefn.accept(visitor_);
	}

	@Override
	public Void visit(ElkHasKeyAxiom elkHasKey) {
		return elkHasKey.accept(visitor_);
	}

	@Override
	public Void visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
		return elkClassAssertionAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkDataPropertyAssertionAxiom elkDataPropertyAssertionAxiom) {
		return elkDataPropertyAssertionAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		return elkDifferentIndividualsAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		return null;
	}

	@Override
	public Void visit(
			ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion) {
		return elkNegativeObjectPropertyAssertion.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom) {
		return elkObjectPropertyAssertionAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		return elkSameIndividualAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkSubAnnotationPropertyOfAxiom subAnnotationPropertyOfAxiom) {
		return subAnnotationPropertyOfAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkAnnotationPropertyDomainAxiom annotationPropertyDomainAxiom) {
		return annotationPropertyDomainAxiom.accept(visitor_);
	}

	@Override
	public Void visit(
			ElkAnnotationPropertyRangeAxiom annotationPropertyRangeAxiom) {
		return annotationPropertyRangeAxiom.accept(visitor_);
	}

	@Override
	public Void visit(ElkAnnotationAssertionAxiom annotationAssertionAxiom) {
		return annotationAssertionAxiom.accept(visitor_);
	}
	


}
