package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LinkableBackwardLinkRule} producing {@link Subsumer}s when
 * processing {@link BackwardLink}s that are propagated over them using
 * {@link Propagation}s contained in the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerBackwardLinkRule extends AbstractLinkableBackwardLinkRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SubsumerBackwardLinkRule.class);

	private static final String NAME_ = "Propagation Over BackwardLink";

	private final Multimap<IndexedPropertyChain, IndexedClassExpression> propagationsByObjectProperty_;

	private SubsumerBackwardLinkRule(LinkableBackwardLinkRule tail) {
		super(tail);
		this.propagationsByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedClassExpression>(
				1);
	}

	public static boolean addRuleFor(Propagation propagation, Context context) {
		SubsumerBackwardLinkRule rule = context.getBackwardLinkRuleChain()
				.getCreate(MATCHER_, FACTORY_);
		return rule.propagationsByObjectProperty_.add(
				propagation.getRelation(), propagation.getCarry());
	}

	public static boolean removeRuleFor(Propagation propagation, Context context) {
		SubsumerBackwardLinkRule rule = context.getBackwardLinkRuleChain()
				.find(MATCHER_);
		return rule == null ? false : rule.propagationsByObjectProperty_
				.remove(propagation.getRelation(), propagation.getCarry());
	}

	public static boolean containsRuleFor(Propagation propagation,
			Context context) {
		SubsumerBackwardLinkRule rule = context.getBackwardLinkRuleChain()
				.find(MATCHER_);
		return rule == null ? false : rule.propagationsByObjectProperty_
				.contains(propagation.getRelation(), propagation.getCarry());
	}

	// TODO: hide this method
	public Multimap<IndexedPropertyChain, IndexedClassExpression> getPropagationsByObjectProperty() {
		return propagationsByObjectProperty_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedClassExpression carry : propagationsByObjectProperty_
				.get(premise.getRelation()))
			producer.produce(premise.getSource(), new ComposedSubsumer(carry));
	}

	boolean addPropagationByObjectProperty(IndexedPropertyChain propRelation,
			IndexedClassExpression conclusion) {
		return propagationsByObjectProperty_.add(propRelation, conclusion);
	}

	boolean removePropagationByObjectProperty(
			IndexedPropertyChain propRelation, IndexedClassExpression conclusion) {
		return propagationsByObjectProperty_.remove(propRelation, conclusion);
	}

	boolean containsPropagationByObjectProperty(
			IndexedPropertyChain propRelation, IndexedClassExpression conclusion) {
		return propagationsByObjectProperty_.contains(propRelation, conclusion);
	}

	static Matcher<LinkableBackwardLinkRule, SubsumerBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<LinkableBackwardLinkRule, SubsumerBackwardLinkRule>(
			SubsumerBackwardLinkRule.class);

	static ReferenceFactory<LinkableBackwardLinkRule, SubsumerBackwardLinkRule> FACTORY_ = new ReferenceFactory<LinkableBackwardLinkRule, SubsumerBackwardLinkRule>() {

		@Override
		public SubsumerBackwardLinkRule create(LinkableBackwardLinkRule tail) {
			return new SubsumerBackwardLinkRule(tail);
		}
	};

	@Override
	public void accept(LinkedBackwardLinkRuleVisitor visitor,
			BackwardLink premise, ContextPremises premises, ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}