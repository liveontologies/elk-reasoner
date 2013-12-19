/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.util;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingUtils {

	public static Subsumer getSubsumerWrapper(IndexedClassExpression ice) {
		return new SubsumerPremise(ice);
	}

	public static BackwardLink getBackwardLinkWrapper(IndexedPropertyChain relation, Context source) {
		return new BackwardLinkPremise(source, relation);
	}

	public static ForwardLink getForwardLinkWrapper(IndexedPropertyChain relation, 	Context target) {
		return new ForwardLinkPremise(target, relation);
	}
}
