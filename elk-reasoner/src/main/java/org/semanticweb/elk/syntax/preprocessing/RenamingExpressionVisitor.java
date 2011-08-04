/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.syntax.preprocessing;

import java.util.ArrayList;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkDeclarationAxiom;
import org.semanticweb.elk.syntax.ElkEntity;
import org.semanticweb.elk.syntax.ElkEntityVisitor;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.ElkNamedIndividual;
import org.semanticweb.elk.syntax.ElkObjectHasValue;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.ElkTransitiveObjectPropertyAxiom;

/**
 * A visitor for creating variants of ElkAxiom, ElkClassExpression and
 * ElkObjectPropertyExpression objects.
 * 
 * @author Markus Kroetzsch
 */
public class RenamingExpressionVisitor implements ElkEntityVisitor<ElkEntity>,
		ElkClassExpressionVisitor<ElkClassExpression>,
		ElkObjectPropertyExpressionVisitor<ElkObjectPropertyExpression>,
		ElkAxiomVisitor<ElkAxiom> {

	protected String postfix;

	public RenamingExpressionVisitor(String postfix) {
		this.postfix = postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public ElkEquivalentClassesAxiom visit(
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		ArrayList<ElkClassExpression> newClassExpressions = new ArrayList<ElkClassExpression>();
		for (ElkClassExpression classExpression : elkEquivalentClassesAxiom
				.getEquivalentClassExpressions()) {
			newClassExpressions.add(classExpression.accept(this));
		}
		return ElkEquivalentClassesAxiom.create(newClassExpressions);
	}

	public ElkSubClassOfAxiom visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
		return ElkSubClassOfAxiom.create(elkSubClassOfAxiom
				.getSubClassExpression().accept(this), elkSubClassOfAxiom
				.getSuperClassExpression().accept(this));
	}

	public ElkClass visit(ElkClass classExpression) {
		return ElkClass.create(classExpression.getIri() + postfix);
	}

	public ElkObjectIntersectionOf visit(ElkObjectIntersectionOf classExpression) {
		ArrayList<ElkClassExpression> newSubClassExpressions = new ArrayList<ElkClassExpression>();
		for (ElkClassExpression subClassExpression : classExpression
				.getClassExpressions()) {
			newSubClassExpressions.add(subClassExpression.accept(this));
		}
		return ElkObjectIntersectionOf.create(newSubClassExpressions);
	}

	public ElkObjectSomeValuesFrom visit(ElkObjectSomeValuesFrom classExpression) {
		ElkClassExpression newClass = classExpression.getClassExpression()
				.accept(this);
		ElkObjectPropertyExpression newProperty = classExpression
				.getObjectPropertyExpression().accept(this);
		return ElkObjectSomeValuesFrom.create(newProperty, newClass);
	}

	public ElkObjectProperty visit(ElkObjectProperty elkObjectProperty) {
		return ElkObjectProperty.create(elkObjectProperty.getIri() + postfix);
	}

	public ElkObjectPropertyExpression visit(
			ElkObjectInverseOf elkObjectInverseOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		return ElkSubObjectPropertyOfAxiom.create(elkSubObjectPropertyOfAxiom
				.getSubObjectPropertyExpression().accept(this),
				elkSubObjectPropertyOfAxiom.getSuperObjectPropertyExpression()
						.accept(this));
	}

	public ElkAxiom visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		return ElkTransitiveObjectPropertyAxiom
				.create(elkTransitiveObjectPropertyAxiom
						.getObjectPropertyExpression().accept(this));
	}

	public ElkDeclarationAxiom visit(ElkDeclarationAxiom elkDeclarationAxiom) {
		return ElkDeclarationAxiom.create(elkDeclarationAxiom.getEntity()
				.accept(this));
	}

	public ElkClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkEntity visit(ElkNamedIndividual elkNamedIndividual) {
		// TODO Auto-generated method stub
		return null;
	}

}
