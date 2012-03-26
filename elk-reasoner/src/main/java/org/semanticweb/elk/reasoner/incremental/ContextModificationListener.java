package org.semanticweb.elk.reasoner.incremental;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.rules.RuleApplicationListener;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;

public class ContextModificationListener implements RuleApplicationListener {

	final Queue<SaturatedClassExpression> modifiedContexts;

	public ContextModificationListener() {
		this.modifiedContexts = new ConcurrentLinkedQueue<SaturatedClassExpression>();
	}

	public Queue<SaturatedClassExpression> getModifiedContexts() {
		return modifiedContexts;
	}

	public void notifyCanProcess() {

	}

	public void notifyCreated(SaturatedClassExpression context) {
		modifiedContexts.add(context);
	}

	public void notifyMofidified(SaturatedClassExpression context) {
		modifiedContexts.add(context);

	}

}
