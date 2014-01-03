package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link ConclusionVisitor} that applies non-redundant rules for the visited
 * {@link Conclusion}s using the provided {@link RuleVisitor} to track rule
 * applications and {@link ConclusionProducer} to output the {@link Conclusion}s
 * of the applied rules. The methods always return {@link true}. This is used to
 * conveniently compose them using a {@link CombinedConclusionVisitor}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class NonRedundantRuleApplicationConclusionVisitor extends
		AbstractConclusionVisitor<Boolean> {

	/**
	 * {@link RuleVisitor} to track rule applications
	 */
	private final RuleVisitor ruleAppVisitor_;

	/**
	 * {@link ConclusionProducer} to produce the {@link Conclusion}s of the
	 * applied rules
	 */
	private final ConclusionProducer producer_;

	public NonRedundantRuleApplicationConclusionVisitor(
			RuleVisitor ruleAppVisitor, ConclusionProducer producer) {
		this.producer_ = producer;
		this.ruleAppVisitor_ = ruleAppVisitor;
	}

	@Override
	public Boolean defaultVisit(Conclusion conclusion, Context context) {
		conclusion.applyNonRedundantRules(ruleAppVisitor_, context, producer_);
		return true;
	}
}
