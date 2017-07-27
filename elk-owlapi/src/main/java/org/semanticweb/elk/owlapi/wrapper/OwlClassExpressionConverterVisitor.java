package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 * An implementation of the visitor pattern for OWL class expressions to convert
 * OWL class expressions to ELK class expressions.
 * 
 * @author "Yevgeny Kazakov"
 */
public class OwlClassExpressionConverterVisitor
		implements OWLClassExpressionVisitorEx<ElkClassExpression> {

	protected static OwlConverter CONVERTER = OwlConverter.getInstance();

	private static OwlClassExpressionConverterVisitor INSTANCE_ = new OwlClassExpressionConverterVisitor();

	public static OwlClassExpressionConverterVisitor getInstance() {
		return INSTANCE_;
	}

	private OwlClassExpressionConverterVisitor() {
	}

	@Override
	public ElkClass visit(OWLClass owlClass) {
		return CONVERTER.convert(owlClass);
	}

	@Override
	public ElkDataAllValuesFrom visit(
			OWLDataAllValuesFrom owlDataAllValuesFrom) {
		return CONVERTER.convert(owlDataAllValuesFrom);
	}

	@Override
	public ElkDataExactCardinality visit(
			OWLDataExactCardinality owlDataExactCardinality) {
		return CONVERTER.convert(owlDataExactCardinality);
	}

	@Override
	public ElkDataHasValue visit(OWLDataHasValue owlDataHasValue) {
		return CONVERTER.convert(owlDataHasValue);
	}

	@Override
	public ElkDataMaxCardinality visit(
			OWLDataMaxCardinality owlDataMaxCardinality) {
		return CONVERTER.convert(owlDataMaxCardinality);
	}

	@Override
	public ElkDataMinCardinality visit(
			OWLDataMinCardinality owlDataMinCardinality) {
		return CONVERTER.convert(owlDataMinCardinality);
	}

	@Override
	public ElkDataSomeValuesFrom visit(
			OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
		return CONVERTER.convert(owlDataSomeValuesFrom);
	}

	@Override
	public ElkObjectAllValuesFrom visit(
			OWLObjectAllValuesFrom owlObjectAllValuesFrom) {
		return CONVERTER.convert(owlObjectAllValuesFrom);
	}

	@Override
	public ElkObjectComplementOf visit(
			OWLObjectComplementOf owlObjectComplementOf) {
		return CONVERTER.convert(owlObjectComplementOf);
	}

	@Override
	public ElkObjectExactCardinality visit(
			OWLObjectExactCardinality owlObjectExactCardinality) {
		return CONVERTER.convert(owlObjectExactCardinality);
	}

	@Override
	public ElkObjectHasSelf visit(OWLObjectHasSelf owlObjectHasSelf) {
		return CONVERTER.convert(owlObjectHasSelf);
	}

	@Override
	public ElkObjectHasValue visit(OWLObjectHasValue owlObjectHasValue) {
		return CONVERTER.convert(owlObjectHasValue);
	}

	@Override
	public ElkObjectIntersectionOf visit(
			OWLObjectIntersectionOf owlObjectIntersectionOf) {
		return CONVERTER.convert(owlObjectIntersectionOf);
	}

	@Override
	public ElkObjectMaxCardinality visit(
			OWLObjectMaxCardinality owlObjectMaxCardinality) {
		return CONVERTER.convert(owlObjectMaxCardinality);
	}

	@Override
	public ElkObjectMinCardinality visit(
			OWLObjectMinCardinality owlObjectMaxCardinality) {
		return CONVERTER.convert(owlObjectMaxCardinality);
	}

	@Override
	public ElkObjectOneOf visit(OWLObjectOneOf owlObjectOneOf) {
		return CONVERTER.convert(owlObjectOneOf);
	}

	@Override
	public ElkObjectSomeValuesFrom visit(
			OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
		return CONVERTER.convert(owlObjectSomeValuesFrom);
	}

	@Override
	public ElkObjectUnionOf visit(OWLObjectUnionOf owlObjectUnionOf) {
		return CONVERTER.convert(owlObjectUnionOf);
	}

}
