/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.util;

import java.util.concurrent.atomic.AtomicReference;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;
import org.semanticweb.elk.util.collections.Operations.Condition;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Premises {

	/**
	 * Returns the first premise of the inference which satisfies the condition,
	 * or null if no such premise exists.
	 * 
	 * @param inference
	 * @param condition
	 * @return
	 */
	public static Conclusion find(Inference inference, final Condition<Conclusion> premiseCondition) {
		final AtomicReference<Conclusion> found = new AtomicReference<Conclusion>();
		
		inference.acceptTraced(new PremiseVisitor<Void, Void>() {

			@Override
			protected Void defaultVisit(Conclusion premise, Void cxt) {
				if (found.get() == null && premiseCondition.holds(premise)) {
					found.set(premise);
				}
				
				return null;
			}
			
		}, null);
		
		return found.get();
	}
}
