/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * Utilities for common saturation tasks
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturationUtils {

	/**
	 * Applies all initialization rules to the context
	 * 
	 * @param context
	 * @param writer
	 * @param index
	 * @param ruleAppVisitor
	 */
	public static void initContext(Context context, BasicSaturationStateWriter writer,
			OntologyIndex index, RuleApplicationVisitor ruleAppVisitor) {
		//TODO register this as a global rule
		//writer.produce(context, new PositiveSubsumer(context.getRoot()));
		// apply all context initialization rules
		LinkRule<Context> initRule = index.getContextInitRuleHead();

		while (initRule != null) {
			initRule.accept(ruleAppVisitor, writer, context);
			initRule = initRule.next();
		}
	}
}
