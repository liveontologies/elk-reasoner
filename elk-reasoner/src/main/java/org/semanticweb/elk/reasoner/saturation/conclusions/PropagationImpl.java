/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PropagationImpl extends AbstractConclusion implements Propagation {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory.getLogger(Propagation.class);

	private final IndexedPropertyChain relation_;

	private final IndexedObjectSomeValuesFrom carry_;

	PropagationImpl(final IndexedPropertyChain relation,
			final IndexedObjectSomeValuesFrom carry) {
		relation_ = relation;
		carry_ = carry;
	}

	@Override
	public String toString() {
		return "Propagation " + relation_ + "->" + carry_;
	}
	
	@Override
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	@Override
	public IndexedObjectSomeValuesFrom getCarry() {
		return carry_;
	}

	@Override
	public void apply(BasicSaturationStateWriter writer, Context context) {
		// propagate over all backward links
		Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();
		Collection<Context> targets = backLinks.get(relation_);
		ConclusionFactory factory = writer.getConclusionFactory();

		for (Context target : targets) {
			//writer.produce(target, new NegativeSubsumerImpl(carry_));
			writer.produce(target, factory.createPropagatedSubsumer(this, relation_, target, context));
		}
	}

	@Override
	public <R, C> R accept(ConclusionVisitor<R, C> visitor, C context) {
		return visitor.visit(this, context);
	}
	
	

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return null;
	}

	@Override
	public boolean addToContextBackwardLinkRule(Context context) {
		return context
				.getBackwardLinkRuleChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_,
						ThisBackwardLinkRule.FACTORY_)
				.addPropagationByObjectProperty(relation_, carry_);
	}

	@Override
	public boolean removeFromContextBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.removePropagationByObjectProperty(relation_,
				carry_) : false;
	}

	@Override
	public boolean containsBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.containsPropagationByObjectProperty(
				relation_, carry_) : false;
	}

	/**
	 * 
	 * 
	 */
	public static class ThisBackwardLinkRule extends
			ModifiableLinkImpl<ModifiableLinkRule<BackwardLink, Context>> implements
			ModifiableLinkRule<BackwardLink, Context> {

		private static final String NAME = "Propagation Over BackwardLink";

		private final Multimap<IndexedPropertyChain, IndexedObjectSomeValuesFrom> propagationsByObjectProperty_;

		ThisBackwardLinkRule(ModifiableLinkRule<BackwardLink, Context> tail) {
			super(tail);
			this.propagationsByObjectProperty_ = new HashSetMultimap<IndexedPropertyChain, IndexedObjectSomeValuesFrom>(
					1);
		}
		
		// TODO: hide this method
		public Multimap<IndexedPropertyChain, IndexedObjectSomeValuesFrom> getPropagationsByObjectProperty() {
			return propagationsByObjectProperty_;
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void apply(BasicSaturationStateWriter writer, BackwardLink link, Context context) {
			LOGGER_.trace("Applying {} to {}", NAME, link);
			
			for (IndexedObjectSomeValuesFrom carry : propagationsByObjectProperty_
					.get(link.getRelation())) {
				//writer.produce(link.getSource(), new NegativeSubsumer(carry));
				writer.produce(link.getSource(), writer.getConclusionFactory().createPropagatedSubsumer(link, carry, context));
			}
		}

		@Override
		public void accept(CompositionRuleApplicationVisitor visitor, BasicSaturationStateWriter writer,
				BackwardLink backwardLink, Context context) {
			visitor.visit(this, writer, backwardLink, context);
		}

		private boolean addPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedObjectSomeValuesFrom conclusion) {
			return propagationsByObjectProperty_.add(propRelation, conclusion);
		}

		private boolean removePropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedObjectSomeValuesFrom conclusion) {
			return propagationsByObjectProperty_.remove(propRelation,
					conclusion);
		}

		private boolean containsPropagationByObjectProperty(
				IndexedPropertyChain propRelation,
				IndexedObjectSomeValuesFrom conclusion) {
			return propagationsByObjectProperty_.contains(propRelation,
					conclusion);
		}

		private static Matcher<ModifiableLinkRule<BackwardLink, Context>, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<ModifiableLinkRule<BackwardLink, Context>, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		private static ReferenceFactory<ModifiableLinkRule<BackwardLink, Context>, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<ModifiableLinkRule<BackwardLink, Context>, ThisBackwardLinkRule>() {

			@Override
			public ThisBackwardLinkRule create(
					ModifiableLinkRule<BackwardLink, Context> tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};

	}
}
