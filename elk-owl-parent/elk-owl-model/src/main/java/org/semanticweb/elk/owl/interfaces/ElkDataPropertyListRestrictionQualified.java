/**
 * 
 */
package org.semanticweb.elk.owl.interfaces;

import java.util.List;

/**
 * Common interface for DataSomeValuesFrom and DataAllValuesFrom
 * restrictions which can be based on a list of data (not object) property expression.
 * 
 * Arity of the datarange <i>must</i> correspond to the number of properties in the list. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public interface ElkDataPropertyListRestrictionQualified extends ElkClassExpression {

	public List<? extends ElkDataPropertyExpression> getDataPropertyExpressions();
	
	public ElkDataRange getDataRange();
}
