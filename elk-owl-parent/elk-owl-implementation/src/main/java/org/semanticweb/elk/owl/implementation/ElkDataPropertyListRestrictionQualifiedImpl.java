/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyListRestrictionQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkDataPropertyListRestrictionQualified}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
abstract public class ElkDataPropertyListRestrictionQualifiedImpl implements ElkDataPropertyListRestrictionQualified {

	final List<? extends ElkDataPropertyExpression> dataProperties;
	final ElkDataRange dataRange;
	
	ElkDataPropertyListRestrictionQualifiedImpl(List<? extends ElkDataPropertyExpression> dataProps,
												ElkDataRange dataRange) {
		this.dataProperties = dataProps;
		this.dataRange = dataRange;
	}
	
	@Override
	public List<? extends ElkDataPropertyExpression> getDataPropertyExpressions() {
		return dataProperties;
	}

	@Override
	public ElkDataRange getDataRange() {
		return dataRange;
	}
	
	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkClassExpressionVisitor<O>)visitor);
	}	
}