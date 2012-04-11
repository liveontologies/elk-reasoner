/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkHasKeyAxiom}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ElkHasKeyAxiomImpl implements ElkHasKeyAxiom {

	private final ElkClassExpression classExpr;
	private final Set<ElkObjectPropertyExpression> objectPropExprs;
	private final Set<ElkDataPropertyExpression> dataPropExprs;
	
	ElkHasKeyAxiomImpl(ElkClassExpression clazz, Set<ElkObjectPropertyExpression> objectPEs, Set<ElkDataPropertyExpression> dataPEs) {
		classExpr = clazz;
		objectPropExprs = objectPEs;
		dataPropExprs = dataPEs;
	}
	
	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ElkClassExpression getClassExpression() {
		return classExpr;
	}

	@Override
	public Set<ElkObjectPropertyExpression> getObjectPropertyExpressions() {
		return objectPropExprs;
	}

	@Override
	public Set<ElkDataPropertyExpression> getDataPropertyExpressions() {
		return dataPropExprs;
	}
}