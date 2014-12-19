package org.semanticweb.elk.reasoner.indexing.conversion;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;

public class FailingElkPolarityExpressionConverter implements
		ElkPolarityExpressionConverter {

	private static <O> O fail(ElkObject expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkClass expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataAllValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataSomeValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectComplementOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectIntersectionOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectOneOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectUnionOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataExactCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMaxCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMinCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectExactCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMinCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataExactCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMaxCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMinCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectExactCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMaxCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMinCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectHasSelf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkDataHasValue expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectAllValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectHasValue expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectSomeValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedIndividual visit(ElkAnonymousIndividual expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedIndividual visit(ElkNamedIndividual expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedObjectProperty visit(ElkObjectInverseOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedObjectProperty visit(ElkObjectProperty expression) {
		return fail(expression);
	}

	@Override
	public ElkPolarityExpressionConverter getComplementaryConverter() {
		return this;
	}

}
