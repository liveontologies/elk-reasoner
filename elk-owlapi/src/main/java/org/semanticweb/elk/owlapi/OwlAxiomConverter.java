/*
 * #%L
 * ELK OWL API
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
/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.syntax.interfaces.ElkAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectFactory;
import org.semanticweb.elk.syntax.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * 
 * Conversion of OWL axioms to ELK axioms
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class OwlAxiomConverter implements OWLAxiomVisitorEx<ElkAxiom> {

	protected final ElkObjectFactory objectFactory;
	protected final OwlClassExpressionConverter classExpressionConverter;
	protected final OwlPropertyExpressionConverter propertyExpressionConverter;
	protected final OwlEntityConverter entityConverter;

	public OwlAxiomConverter(ElkObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
		this.propertyExpressionConverter = new OwlPropertyExpressionConverter(
				objectFactory);
		this.classExpressionConverter = new OwlClassExpressionConverter(
				objectFactory, this.propertyExpressionConverter);
		this.entityConverter = new OwlEntityConverter(objectFactory,
				this.classExpressionConverter, this.propertyExpressionConverter);
	}

	public OwlAxiomConverter(ElkObjectFactory objectFactory,
			OwlClassExpressionConverter classExpressionConverter,
			OwlPropertyExpressionConverter propertyExpressionConverter,
			OwlEntityConverter entityConverter) {
		this.objectFactory = objectFactory;
		this.propertyExpressionConverter = propertyExpressionConverter;
		this.classExpressionConverter = classExpressionConverter;
		this.entityConverter = entityConverter;
	}

	public ElkAxiom visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLAnnotationPropertyDomainAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLAnnotationPropertyRangeAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkSubClassOfAxiom visit(OWLSubClassOfAxiom axiom) {
		return objectFactory.getSubClassOfAxiom(
				axiom.getSubClass().accept(classExpressionConverter), axiom
						.getSuperClass().accept(classExpressionConverter));
	}

	public ElkAxiom visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLReflexiveObjectPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDisjointClassesAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDataPropertyDomainAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLObjectPropertyDomainAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDifferentIndividualsAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDisjointDataPropertiesAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDisjointObjectPropertiesAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLObjectPropertyRangeAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLObjectPropertyAssertionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLFunctionalObjectPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkSubObjectPropertyOfAxiom visit(OWLSubObjectPropertyOfAxiom axiom) {
		return objectFactory.getSubObjectPropertyOfAxiom(axiom.getSubProperty()
				.accept(propertyExpressionConverter), axiom.getSuperProperty()
				.accept(propertyExpressionConverter));
	}

	public ElkAxiom visit(OWLDisjointUnionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDeclarationAxiom axiom) {
		return objectFactory.getDeclarationAxiom(axiom.getEntity().accept(
				entityConverter));
	}

	public ElkAxiom visit(OWLAnnotationAssertionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLSymmetricObjectPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDataPropertyRangeAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLFunctionalDataPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLEquivalentDataPropertiesAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLClassAssertionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkEquivalentClassesAxiom visit(OWLEquivalentClassesAxiom axiom) {
		List<OWLClassExpression> owlClassExpressions = axiom
				.getClassExpressionsAsList();
		List<ElkClassExpression> elkClassExpressions = new ArrayList<ElkClassExpression>();
		for (OWLClassExpression owlClassExpression : owlClassExpressions) {
			elkClassExpressions.add(owlClassExpression
					.accept(classExpressionConverter));
		}
		return objectFactory.getEquivalentClassesAxiom(elkClassExpressions);
	}

	public ElkAxiom visit(OWLDataPropertyAssertionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkTransitiveObjectPropertyAxiom visit(
			OWLTransitiveObjectPropertyAxiom axiom) {
		return objectFactory.getTransitiveObjectPropertyAxiom(axiom
				.getProperty().accept(propertyExpressionConverter));
	}

	public ElkAxiom visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLSubDataPropertyOfAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLSameIndividualAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLSubPropertyChainOfAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLInverseObjectPropertiesAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLHasKeyAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(OWLDatatypeDefinitionAxiom axiom) {
		// TODO Support this axiom type
		throw new ConverterException(axiom.getAxiomType().getName()
				+ " not supported");
	}

	public ElkAxiom visit(SWRLRule rule) {
		// TODO Support this axiom type
		throw new ConverterException(rule.getAxiomType().getName()
				+ " not supported");
	}

}
