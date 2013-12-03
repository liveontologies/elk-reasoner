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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule0;
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
public class Propagation extends AbstractConclusion {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory.getLogger(Propagation.class);

	private final IndexedPropertyChain relation_;

	private final IndexedObjectSomeValuesFrom carry_;

	Propagation(final IndexedPropertyChain relation,
			final IndexedObjectSomeValuesFrom carry) {
		relation_ = relation;
		carry_ = carry;
	}

	@Override
	public String toString() {
		return "Propagation " + relation_ + "->" + carry_;
	}
	
	public IndexedClassExpression getCarry() {
		return carry_;
	}

	public void apply(BasicSaturationStateWriter writer, Context context) {
		// propagate over all backward links
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		Collection<Context> targets = backLinks.get(relation_);

		for (Context target : targets) {
			writer.produce(target, new NegativeSubsumer(carry_));
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}
	
	

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return null;
	}

	public boolean addToContextBackwardLinkRule(Context context) {
		return context
				.getBackwardLinkRuleChain()
				.getCreate(ThisBackwardLinkRule.MATCHER_,
						ThisBackwardLinkRule.FACTORY_)
				.addPropagationByObjectProperty(relation_, carry_);
	}

	public boolean removeFromContextBackwardLinkRule(Context context) {
		ThisBackwardLinkRule rule = context.getBackwardLinkRuleChain().find(
				ThisBackwardLinkRule.MATCHER_);

		return rule != null ? rule.removePropagationByObjectProperty(relation_,
				carry_) : false;
	}

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
			ModifiableLinkImpl<ModifiableLinkRule0<BackwardLink>> implements
			ModifiableLinkRule0<BackwardLink> {

		private static final String NAME = "Propagation Over BackwardLink";

		private final Multimap<IndexedPropertyChain, IndexedObjectSomeValuesFrom> propagationsByObjectProperty_;

		ThisBackwardLinkRule(ModifiableLinkRule0<BackwardLink> tail) {
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
		public void apply(BasicSaturationStateWriter writer, BackwardLink link) {
			LOGGER_.trace("Applying {} to {}", NAME, link);
			
			for (IndexedObjectSomeValuesFrom carry : propagationsByObjectProperty_
					.get(link.getRelation())) {
				//writer.produce(link.getSource(), new NegativeSubsumer(carry));
				writer.produce(link.getSource(), writer.getConclusionFactory().existentialInference(link, carry));
			}
		}

		@Override
		public void accept(CompositionRuleApplicationVisitor visitor, BasicSaturationStateWriter writer,
				BackwardLink backwardLink) {
			visitor.visit(this, writer, backwardLink);
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

		private static Matcher<ModifiableLinkRule0<BackwardLink>, ThisBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<ModifiableLinkRule0<BackwardLink>, ThisBackwardLinkRule>(
				ThisBackwardLinkRule.class);

		private static ReferenceFactory<ModifiableLinkRule0<BackwardLink>, ThisBackwardLinkRule> FACTORY_ = new ReferenceFactory<ModifiableLinkRule0<BackwardLink>, ThisBackwardLinkRule>() {

			@Override
			public ThisBackwardLinkRule create(
					ModifiableLinkRule0<BackwardLink> tail) {
				return new ThisBackwardLinkRule(tail);
			}
		};

	}
}
