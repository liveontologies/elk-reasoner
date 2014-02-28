package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ContextInitialization}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextInitializationImpl extends AbstractConclusion implements
		ContextInitialization {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextInitializationImpl.class);

	// actually we just need only context initialization rules,
	// but they can change after creating this object
	private final OntologyIndex ontologyIndex_;

	public ContextInitializationImpl(OntologyIndex ontologyIndex) {
		this.ontologyIndex_ = ontologyIndex;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		LinkedContextInitRule rule = ontologyIndex_.getContextInitRuleHead();
		LOGGER_.trace("applying init rules:");
		while (rule != null) {
			LOGGER_.trace("init rule: {}", rule.getName());
			rule.accept(ruleAppVisitor, this, premises, producer);
			rule = rule.next();
		}
	}

	@Override
	public String toString() {
		return "Init";
	}

	@Override
	public LinkedContextInitRule getContextInitRuleHead() {
		return ontologyIndex_.getContextInitRuleHead();
	}

}
