/**
 * @author Yevgeny Kazakov, Jul 1, 2011
 */
package org.semanticweb.elk.owlapi;

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

/**
 * @author Yevgeny Kazakov
 * 
 */
public final class ElkClassExpressionConverter implements
		ElkClassExpressionVisitor<OWLClassExpression> {

	final OWLDataFactory owlDataFactory = OWLManager.getOWLDataFactory();
	private static final ElkClassExpressionConverter converter = new ElkClassExpressionConverter();

	private ElkClassExpressionConverter() {
	}

	static ElkClassExpressionConverter getInstance() {
		return converter;
	}

	public OWLClass visit(ElkClass elkClass) {
		String iri = elkClass.getIri();
		if (elkClass.equals(ElkClass.ELK_OWL_THING))
			return owlDataFactory.getOWLThing();
		else if (elkClass.equals(ElkClass.ELK_OWL_NOTHING))
			return owlDataFactory.getOWLNothing();
		else
			return owlDataFactory.getOWLClass(IRI.create(iri));
	}

	public OWLObjectIntersectionOf visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		// TODO Support this constructor
		throw new ConverterException("Not yet implemented.");
	}

	public OWLObjectSomeValuesFrom visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		// TODO Support this constructor
		throw new ConverterException("Not yet implemented.");
	}

}
