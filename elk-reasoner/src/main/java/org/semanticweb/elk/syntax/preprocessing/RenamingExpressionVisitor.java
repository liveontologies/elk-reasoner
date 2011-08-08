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

import org.semanticweb.elk.syntax.ElkAxiomVisitor;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkEntityVisitor;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.syntax.implementation.ElkClassImpl;
import org.semanticweb.elk.syntax.implementation.ElkDeclarationAxiomImpl;
import org.semanticweb.elk.syntax.implementation.ElkEquivalentClassesAxiomImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectIntersectionOfImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectPropertyImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectSomeValuesFromImpl;
import org.semanticweb.elk.syntax.implementation.ElkSubClassOfAxiomImpl;
import org.semanticweb.elk.syntax.implementation.ElkSubObjectPropertyOfAxiomImpl;
import org.semanticweb.elk.syntax.implementation.ElkTransitiveObjectPropertyAxiomImpl;
import org.semanticweb.elk.syntax.interfaces.ElkAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkEntity;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.syntax.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectProperty;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkTransitiveObjectPropertyAxiom;

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

	class RenamingSubObjectPropertyExpressionVisitor
			implements
			ElkSubObjectPropertyExpressionVisitor<ElkSubObjectPropertyExpression> {

		public ElkSubObjectPropertyExpression visit(
				ElkObjectProperty elkObjectProperty) {
			// TODO Auto-generated method stub
			return null;
		}

		public ElkSubObjectPropertyExpression visit(
				ElkObjectInverseOf elkObjectInverseOf) {
			// TODO Auto-generated method stub
			return null;
		}

		public ElkSubObjectPropertyExpression visit(
				ElkObjectPropertyChain elkObjectPropertyChain) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	protected RenamingSubObjectPropertyExpressionVisitor renamingSubObjectPropertyExpressionVisitor;

	protected String postfix;

	public RenamingExpressionVisitor(String postfix) {
		this.postfix = postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
		this.renamingSubObjectPropertyExpressionVisitor = new RenamingSubObjectPropertyExpressionVisitor();
	}

	public ElkEquivalentClassesAxiom visit(
			ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		ArrayList<ElkClassExpression> newClassExpressions = new ArrayList<ElkClassExpression>();
		for (ElkClassExpression classExpression : elkEquivalentClassesAxiom
				.getClassExpressions()) {
			newClassExpressions.add(classExpression.accept(this));
		}
		return ElkEquivalentClassesAxiomImpl.create(newClassExpressions);
	}

	public ElkSubClassOfAxiom visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
		return ElkSubClassOfAxiomImpl.create(elkSubClassOfAxiom
				.getSubClassExpression().accept(this), elkSubClassOfAxiom
				.getSuperClassExpression().accept(this));
	}

	public ElkClass visit(ElkClass classExpression) {
		return ElkClassImpl.create(classExpression.getIri() + postfix);
	}

	public ElkObjectIntersectionOf visit(ElkObjectIntersectionOf classExpression) {
		ArrayList<ElkClassExpression> newSubClassExpressions = new ArrayList<ElkClassExpression>();
		for (ElkClassExpression subClassExpression : classExpression
				.getClassExpressions()) {
			newSubClassExpressions.add(subClassExpression.accept(this));
		}
		return ElkObjectIntersectionOfImpl.create(newSubClassExpressions);
	}

	public ElkObjectSomeValuesFrom visit(ElkObjectSomeValuesFrom classExpression) {
		ElkClassExpression newClass = classExpression.getClassExpression()
				.accept(this);
		ElkObjectPropertyExpression newProperty = classExpression
				.getObjectPropertyExpression().accept(this);
		return ElkObjectSomeValuesFromImpl.create(newProperty, newClass);
	}

	public ElkObjectProperty visit(ElkObjectProperty elkObjectProperty) {
		return ElkObjectPropertyImpl.create(elkObjectProperty.getIri()
				+ postfix);
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
		return ElkSubObjectPropertyOfAxiomImpl
				.create(elkSubObjectPropertyOfAxiom
						.getSubObjectPropertyExpression()
						.accept(this.renamingSubObjectPropertyExpressionVisitor),
						elkSubObjectPropertyOfAxiom
								.getSuperObjectPropertyExpression()
								.accept(this));
	}

	public ElkAxiom visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		return ElkTransitiveObjectPropertyAxiomImpl
				.create(elkTransitiveObjectPropertyAxiom
						.getObjectPropertyExpression().accept(this));
	}

	public ElkDeclarationAxiom visit(ElkDeclarationAxiom elkDeclarationAxiom) {
		return ElkDeclarationAxiomImpl.create(elkDeclarationAxiom.getEntity()
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

	public ElkClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkSubObjectPropertyExpression visit(
			ElkObjectPropertyChain elkObjectPropertyChain) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkDisjointClassesAxiom elkDisjointClasses) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

}
