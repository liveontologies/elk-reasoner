/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;

/**
 * Provides access to package protected methods of {@link AbstractReasonerState} for testing purposes.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReasonerStateAccessor {
	
	public static TraceState getTraceState(AbstractReasonerState reasoner) {
		return reasoner.getTraceState();
	}
	
	public static IndexedClassExpression transform(AbstractReasonerState reasoner, ElkClassExpression ce) {
		return reasoner.transform(ce);
	}
}
