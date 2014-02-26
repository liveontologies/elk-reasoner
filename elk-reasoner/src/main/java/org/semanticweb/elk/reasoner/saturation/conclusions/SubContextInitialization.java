package org.semanticweb.elk.reasoner.saturation.conclusions;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.SubConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@code Conclusion} indicating that a {@link SubContext} of a
 * {@link Context} where it is stored should be initialized.
 * 
 * @author "Yevgeny Kazakov"
 */
public class SubContextInitialization extends AbstractConclusion implements
		Conclusion, SubConclusion {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SubContextInitialization.class);

	public static final String NAME = "Sub-Context Initialization";

	/**
	 * the sub-root of the {@link SubContext} that should be initialized
	 */
	private final IndexedObjectProperty subRoot_;

	public SubContextInitialization(IndexedObjectProperty subRoot) {
		this.subRoot_ = subRoot;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return accept((SubConclusionVisitor<I, O>) visitor, input);
	}

	@Override
	public <I, O> O accept(SubConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		LOGGER_.trace("{}::{} applying sub-concept init rules:",
				premises.getRoot(), subRoot_);
		PropagationInitializationRule.getInstance().accept(ruleAppVisitor,
				this, premises, producer);
	}

	@Override
	public IndexedObjectProperty getSubRoot() {
		return subRoot_;
	}

	@Override
	public String toString() {
		return "SubInit(" + subRoot_ + ")";
	}

}
