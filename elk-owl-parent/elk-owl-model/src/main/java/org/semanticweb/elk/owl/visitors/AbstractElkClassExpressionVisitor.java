/**
 * 
 */
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;

/**
 * A skeleton implementation.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractElkClassExpressionVisitor<O> implements
		ElkClassExpressionVisitor<O> {

	protected abstract O defaultVisit(ElkClassExpression ce);
	
	@Override
	public O visit(ElkClass elkClass) {
		return defaultVisit(elkClass);
	}

	@Override
	public O visit(ElkDataAllValuesFrom ce) {
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataExactCardinality ce) {
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkDataExactCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataHasValue ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMaxCardinality ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMaxCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMinCardinality ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataMinCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkDataSomeValuesFrom ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectAllValuesFrom ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectComplementOf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectExactCardinality ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkObjectExactCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectHasSelf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectHasValue ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectIntersectionOf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectMaxCardinality ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkObjectMaxCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectMinCardinality ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(
			ElkObjectMinCardinalityQualified ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectOneOf ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectSomeValuesFrom ce) {
		
		return defaultVisit(ce);
	}

	@Override
	public O visit(ElkObjectUnionOf ce) {
		
		return defaultVisit(ce);
	}

}
