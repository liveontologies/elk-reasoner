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
 * A {@link ConclusionVisitor} that applies rules for visited {@link Conclusion}
 * s using the provided {@link RuleVisitor}s and {@link ConclusionProducer}s for
 * respectively non-redundant and redundant rule applications. Essentially, it
 * just calls
 * {@link Conclusion#applyNonRedundantRules(RuleVisitor, Context, ConclusionProducer)}
 * and
 * {@link Conclusion#applyRedundantRules(RuleVisitor, Context, ConclusionProducer)}
 * using the respective parameters.
 * 
 * @see AllRuleApplicationConclusionVisitor
 * @see NonRedundantRuleApplicationConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class HybridRuleApplicationConclusionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	/**
	 * {@link RuleVisitor} for non-redundant rule applications
	 */
	private final RuleVisitor nonRedundantRuleVisitor_;

	/**
	 * {@link RuleVisitor} for redundant rule applications
	 */
	private final RuleVisitor redundantRuleVisitor_;

	/**
	 * {@link ConclusionProducer} to produce the {@link Conclusion}s of the
	 * non-redundant rules
	 */
	private final ConclusionProducer nonRedundantProducer_;

	/**
	 * {@link ConclusionProducer} to produce the {@link Conclusion}s of the
	 * redundant rules
	 */
	private final ConclusionProducer redundantProducer_;

	public HybridRuleApplicationConclusionVisitor(
			RuleVisitor nonRedundantRuleVisitor,
			RuleVisitor redundantRuleVisitor,
			ConclusionProducer nonRedundantProducer,
			ConclusionProducer redundantProducer) {
		this.nonRedundantRuleVisitor_ = nonRedundantRuleVisitor;
		this.redundantRuleVisitor_ = redundantRuleVisitor;
		this.nonRedundantProducer_ = nonRedundantProducer;
		this.redundantProducer_ = redundantProducer;
	}

	@Override
	public Boolean defaultVisit(Conclusion conclusion, Context context) {
		conclusion.applyNonRedundantRules(nonRedundantRuleVisitor_, context,
				nonRedundantProducer_);
		conclusion.applyRedundantRules(redundantRuleVisitor_, context,
				redundantProducer_);
		return true;
	}
}
