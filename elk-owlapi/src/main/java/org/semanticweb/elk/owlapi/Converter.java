/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.ElkTransitiveObjectPropertyAxiom;
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
	
	public static OWLClassNodeSet convert(List<ClassNode> nodes) {
		Set<Node<OWLClass>> owlNodes = new HashSet<Node<OWLClass>>();
		for (ClassNode node : nodes) {
			owlNodes.add(convert(node));					
		}
		return new OWLClassNodeSet(owlNodes);
	}	

}
