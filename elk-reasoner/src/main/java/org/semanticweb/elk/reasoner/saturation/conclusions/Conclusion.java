package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

/**
 * A general type of conclusions, produced by inference rules. This is the main
 * type of information that is exchanged between {@link Context}s. When a
 * {@link Conclusion} has been derived for a particular {@link Context}, it
 * should be processed within this context.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Conclusion {

	public <R> R accept(ConclusionVisitor<R> visitor, Context context);

	/**
	 * 
	 * @param contextWhereStored
	 * @return The context which this conclusion is logically relevant for; it
	 *         cannot be {@code null}
	 */
	public Context getSourceContext(Context contextWhereStored);

	/**
	 * Apply all non-redundant inferences for this {@link Conclusion} in a
	 * {@link Context}
	 * 
	 * @see #applyRedundantRules(RuleVisitor, Context, ConclusionProducer)
	 * 
	 * @param ruleAppVisitor
	 * @param context
	 * @param producer
	 */
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			Context context, ConclusionProducer producer);

	/**
	 * Apply all redundant rules for this {@link Conclusion} in a
	 * {@link Context}. A rule is redundant if its application is not necessary
	 * for completeness. Redundancy of a rule depends on other conclusions
	 * contained in the {@link Context}: a non-redundant rule might become
	 * redundant when other {@link Conclusion} are added to the {@link Context}.
	 * 
	 * @param ruleAppVisitor
	 * @param context
	 * @param producer
	 */
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			Context context, ConclusionProducer producer);
}
