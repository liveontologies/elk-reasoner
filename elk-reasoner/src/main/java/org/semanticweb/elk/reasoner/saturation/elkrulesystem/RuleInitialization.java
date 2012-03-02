package org.semanticweb.elk.reasoner.saturation.elkrulesystem;

import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;

public class RuleInitialization<C extends ContextElClassSaturation> implements InferenceRule<C> {

	public void init(C context, RuleApplicationEngine engine) {
		engine.enqueue(context, new PositiveSuperClassExpression<ContextElClassSaturation>(context.getRoot()));

		if (engine.owlThing.occursNegatively())
			engine.enqueue(context, new PositiveSuperClassExpression<ContextElClassSaturation>(engine.owlThing));
		
	}
	
}
