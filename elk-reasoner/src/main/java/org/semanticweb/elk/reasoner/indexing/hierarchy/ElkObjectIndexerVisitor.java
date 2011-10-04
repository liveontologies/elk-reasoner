package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkClass;
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
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;

public class ElkObjectIndexerVisitor implements
		ElkClassExpressionVisitor<IndexedClassExpression>,
		ElkObjectPropertyExpressionVisitor<IndexedObjectProperty> {

	private IndexedObjectFilter subObjectFilter;

	ElkObjectIndexerVisitor(IndexedObjectFilter subObjectFilter) {
		this.subObjectFilter = subObjectFilter;
	}

	public IndexedClassExpression visit(ElkClass elkClass) {
		return subObjectFilter.filter(new IndexedClass(elkClass));
	}

	public IndexedClassExpression visit(
			ElkObjectAllValuesFrom elkObjectAllValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkObjectExactCardinality elkObjectExactCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(ElkObjectHasSelf elkObjectHasSelf) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(ElkObjectHasValue elkObjectHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkObjectMaxCardinality elkObjectMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkObjectMinCardinality elkObjectMinCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return subObjectFilter.filter(new IndexedObjectSomeValuesFrom(
				elkObjectSomeValuesFrom.getObjectPropertyExpression().accept(
						this), elkObjectSomeValuesFrom.getClassExpression()
						.accept(this)));
	}

	public IndexedClassExpression visit(ElkObjectUnionOf elkObjectUnionOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(ElkDataHasValue elkDataHasValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkDataMaxCardinality elkDataMaxCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkDataMinCardinality elkDataMinCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkDataExactCardinality elkDataExactCardinality) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkDataSomeValuesFrom elkDataSomeValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedClassExpression visit(
			ElkDataAllValuesFrom elkDataAllValuesFrom) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
		// TODO Auto-generated method stub
		return null;
	}

	public IndexedObjectProperty visit(ElkObjectProperty elkObjectProperty) {
		// TODO Auto-generated method stub
		return null;
	}

}
