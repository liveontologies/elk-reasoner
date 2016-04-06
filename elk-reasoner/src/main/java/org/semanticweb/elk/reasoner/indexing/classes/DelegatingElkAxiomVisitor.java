/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.classes;
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
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

/**
 * Delegates all visit method calls to the underlying visitor.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 * 
 * @param <O>
 *            the type of the output of this visitor
 */
class DelegatingElkAxiomVisitor<O> implements ElkAxiomVisitor<O>{

	private final ElkAxiomVisitor<O> visitor_;
	
	public DelegatingElkAxiomVisitor(ElkAxiomVisitor<O> visitor) {
		this.visitor_ = visitor;
	}
	
	protected ElkAxiomVisitor<O> getVisitor() {
		return visitor_;
	}

	@Override
	public O visit(ElkSWRLRule axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkSubClassOfAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDisjointUnionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkClassAssertionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkSameIndividualAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDisjointClassesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyRangeAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkSubDataPropertyOfAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyDomainAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkAnnotationAssertionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyAssertionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDeclarationAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDisjointDataPropertiesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkFunctionalDataPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkAnnotationPropertyRangeAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkInverseObjectPropertiesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkSubAnnotationPropertyOfAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkSymmetricObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkAnnotationPropertyDomainAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkAsymmetricObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDisjointObjectPropertiesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkEquivalentDataPropertiesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkFunctionalObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkIrreflexiveObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkHasKeyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkNegativeDataPropertyAssertionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		return visitor_.visit(axiom);
	}

	@Override
	public O visit(ElkDatatypeDefinitionAxiom axiom) {
		return visitor_.visit(axiom);
	}

		
}
