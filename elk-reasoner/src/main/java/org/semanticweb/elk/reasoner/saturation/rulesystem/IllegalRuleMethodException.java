package org.semanticweb.elk.reasoner.saturation.rulesystem;

import java.lang.reflect.Method;

/**
 * A simple exception to report problems when registering inference rules in
 * InferenceRuleManager.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class IllegalRuleMethodException extends Exception {

	/**
	 * The version identifier for this Serializable class. Increment only if the
	 * <i>serialized</i> form of the class changes.
	 */
	private static final long serialVersionUID = 1L;

	public IllegalRuleMethodException(Method ruleMethod, String message) {
		super("Cannot use the rule method '" + ruleMethod + "'.\n" + message);
	}
	
}
