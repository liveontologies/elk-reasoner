/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface TracingTestVisitor {

	public boolean visit(ElkClassExpression subsumee, ElkClassExpression subsumer);
}
