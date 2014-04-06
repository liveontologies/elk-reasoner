/**
 * 
 */
package org.semanticweb.elk.alc.indexing.hierarchy;

import java.util.Arrays;

import org.semanticweb.elk.alc.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.owl.implementation.ElkObjectIntersectionOfImpl;
import org.semanticweb.elk.owl.implementation.ElkObjectSomeValuesFromImpl;
import org.semanticweb.elk.owl.implementation.ElkObjectUnionOfImpl;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ClassExpressionDeindexer implements
		IndexedClassExpressionVisitor<ElkClassExpression> {

	@Override
	public ElkClassExpression visit(IndexedClass element) {
		return element.getElkClass();
	}

	@Override
	public ElkClassExpression visit(IndexedObjectIntersectionOf element) {
		return new ElkObjectIntersectionOfImpl(Arrays.asList(element.getFirstConjunct().accept(this), element.getSecondConjunct().accept(this)));
	}

	@Override
	public ElkClassExpression visit(IndexedObjectUnionOf element) {
		return new ElkObjectUnionOfImpl(Arrays.asList(element.getFirstDisjunct().accept(this), element.getSecondDisjunct().accept(this)));

	}

	@Override
	public ElkClassExpression visit(IndexedObjectSomeValuesFrom element) {
		return new ElkObjectSomeValuesFromImpl(element.getRelation().getElkObjectProperty(), element.getFiller().accept(this));
	}

}
