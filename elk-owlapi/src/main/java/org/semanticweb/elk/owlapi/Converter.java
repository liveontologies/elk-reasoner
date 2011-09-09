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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

/**
 * Facade class for conversion between OWL and ELK objects.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class Converter {

	protected final ElkObjectFactory objectFactory;
	protected final OwlClassExpressionConverter classExpressionConverter;
	protected final OwlPropertyExpressionConverter propertyExpressionConverter;
	protected final OwlEntityConverter entityConverter;
	protected final OwlAxiomConverter axiomConverter;
	
	protected final ElkClassExpressionConverter elkClassExpressionConverter;

	public Converter(ElkObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
		this.propertyExpressionConverter = new OwlPropertyExpressionConverter(
				objectFactory);
		this.classExpressionConverter = new OwlClassExpressionConverter(
				objectFactory, this.propertyExpressionConverter);
		this.entityConverter = new OwlEntityConverter(objectFactory,
				this.classExpressionConverter, this.propertyExpressionConverter);
		this.axiomConverter = new OwlAxiomConverter(objectFactory,
				classExpressionConverter, propertyExpressionConverter,
				entityConverter);
		this.elkClassExpressionConverter = new ElkClassExpressionConverter(objectFactory);
	}

	/* OWL to ELK */

	public ElkClass convert(OWLClass cls) {
		return classExpressionConverter.visit(cls);
	}

	public ElkObjectIntersectionOf convert(OWLObjectIntersectionOf ce) {
		return classExpressionConverter.visit(ce);
	}

	public ElkObjectSomeValuesFrom convert(OWLObjectSomeValuesFrom ce) {
		return classExpressionConverter.visit(ce);
	}

	public ElkClassExpression convert(OWLClassExpression ce) {
		return ce.accept(classExpressionConverter);
	}

	public ElkObjectProperty convert(OWLObjectProperty op) {
		return propertyExpressionConverter.visit(op);
	}

	public ElkObjectPropertyExpression convert(OWLObjectPropertyExpression pe) {
		return pe.accept(propertyExpressionConverter);
	}

	public ElkEquivalentClassesAxiom convert(OWLEquivalentClassesAxiom ax) {
		return axiomConverter.visit(ax);
	}

	public ElkSubClassOfAxiom convert(OWLSubClassOfAxiom ax) {
		return axiomConverter.visit(ax);
	}

	public ElkSubObjectPropertyOfAxiom convert(
			OWLSubObjectPropertyOfAxiom ax) {
		return axiomConverter.visit(ax);
	}

	public ElkTransitiveObjectPropertyAxiom convert(
			OWLTransitiveObjectPropertyAxiom ax) {
		return axiomConverter.visit(ax);
	}

	public ElkAxiom convert(OWLAxiom ax) {
		return ax.accept(axiomConverter);
	}

	/* ELK to OWL */

	public OWLClass convert(ElkClass cls) {
		return elkClassExpressionConverter.visit(cls);
	}

	public OWLObjectIntersectionOf convert(ElkObjectIntersectionOf ce) {
		return elkClassExpressionConverter.visit(ce);
	}

	public OWLObjectSomeValuesFrom convert(ElkObjectSomeValuesFrom ce) {
		return elkClassExpressionConverter.visit(ce);
	}

	public OWLClassExpression convert(ElkClassExpression ce) {
		return ce.accept(elkClassExpressionConverter);
	}

	public OWLClassNode convert(ClassNode node) {
		Set<OWLClass> owlClasses = new HashSet<OWLClass>();
		for (ElkClass cls : node.getMembers()) {
			owlClasses.add(convert(cls));
		}
		return new OWLClassNode(owlClasses);
	}

	public OWLClassNodeSet convert(Iterable<ClassNode> nodes) {
		Set<Node<OWLClass>> owlNodes = new HashSet<Node<OWLClass>>();
		for (ClassNode node : nodes) {
			owlNodes.add(convert(node));
		}
		return new OWLClassNodeSet(owlNodes);
	}

}
