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

import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.elk.syntax.interfaces.ElkAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectProperty;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkTransitiveObjectPropertyAxiom;
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
 * 
 */
public class Converter {

	/* OWL to ELK */

	public static ElkClass convert(OWLClass cls) {
		OwlClassExpressionConverter converter = OwlClassExpressionConverter
				.getInstance();
		return converter.visit(cls);
	}

	public static ElkObjectIntersectionOf convert(OWLObjectIntersectionOf ce) {
		OwlClassExpressionConverter converter = OwlClassExpressionConverter
				.getInstance();
		return converter.visit(ce);
	}

	public static ElkObjectSomeValuesFrom convert(OWLObjectSomeValuesFrom ce) {
		OwlClassExpressionConverter converter = OwlClassExpressionConverter
				.getInstance();
		return converter.visit(ce);
	}

	public static ElkClassExpression convert(OWLClassExpression ce) {
		OwlClassExpressionConverter converter = OwlClassExpressionConverter
				.getInstance();
		return ce.accept(converter);
	}

	public static ElkObjectProperty convert(OWLObjectProperty op) {
		OwlPropertyExpressionConverter converter = OwlPropertyExpressionConverter
				.getInstance();
		return converter.visit(op);
	}

	public static ElkObjectPropertyExpression convert(
			OWLObjectPropertyExpression pe) {
		OwlPropertyExpressionConverter converter = OwlPropertyExpressionConverter
				.getInstance();
		return pe.accept(converter);
	}

	public static ElkEquivalentClassesAxiom convert(OWLEquivalentClassesAxiom ax) {
		OwlAxiomConverter converter = OwlAxiomConverter.getInstance();
		return converter.visit(ax);
	}

	public static ElkSubClassOfAxiom convert(OWLSubClassOfAxiom ax) {
		OwlAxiomConverter converter = OwlAxiomConverter.getInstance();
		return converter.visit(ax);
	}

	public static ElkSubObjectPropertyOfAxiom convert(
			OWLSubObjectPropertyOfAxiom ax) {
		OwlAxiomConverter converter = OwlAxiomConverter.getInstance();
		return converter.visit(ax);
	}

	public static ElkTransitiveObjectPropertyAxiom convert(
			OWLTransitiveObjectPropertyAxiom ax) {
		OwlAxiomConverter converter = OwlAxiomConverter.getInstance();
		return converter.visit(ax);
	}

	public static ElkAxiom convert(OWLAxiom ax) {
		OwlAxiomConverter converter = OwlAxiomConverter.getInstance();
		return ax.accept(converter);
	}

	/* ELK to OWL */

	public static OWLClass convert(ElkClass cls) {
		ElkClassExpressionConverter converter = ElkClassExpressionConverter
				.getInstance();
		return converter.visit(cls);
	}

	public static OWLObjectIntersectionOf convert(ElkObjectIntersectionOf ce) {
		ElkClassExpressionConverter converter = ElkClassExpressionConverter
				.getInstance();
		return converter.visit(ce);
	}

	public static OWLObjectSomeValuesFrom convert(ElkObjectSomeValuesFrom ce) {
		ElkClassExpressionConverter converter = ElkClassExpressionConverter
				.getInstance();
		return converter.visit(ce);
	}

	public static OWLClassExpression convert(ElkClassExpression ce) {
		ElkClassExpressionConverter converter = ElkClassExpressionConverter
				.getInstance();
		return ce.accept(converter);
	}
	
	public static OWLClassNode convert(ClassNode node) {
		Set<OWLClass> owlClasses = new HashSet<OWLClass>();
		for (ElkClass cls : node.getMembers()) {
			owlClasses.add(convert(cls));
		}
		return new OWLClassNode(owlClasses);
	}
	
	public static OWLClassNodeSet convert(Iterable<ClassNode> nodes) {
		Set<Node<OWLClass>> owlNodes = new HashSet<Node<OWLClass>>();
		for (ClassNode node : nodes) {
			owlNodes.add(convert(node));					
		}
		return new OWLClassNodeSet(owlNodes);
	}	

}
