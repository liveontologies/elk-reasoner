/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
