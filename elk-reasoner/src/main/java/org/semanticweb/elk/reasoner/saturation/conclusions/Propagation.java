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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.SubConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.NonReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.ReflexivePropagationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class Propagation extends AbstractConclusion implements Conclusion,
		SubConclusion {

	// logger for this class
	static final Logger LOGGER_ = LoggerFactory.getLogger(Propagation.class);

	public static final String NAME = "Propagation";

	private final IndexedObjectProperty relation_;

	private final IndexedObjectSomeValuesFrom carry_;

	public Propagation(IndexedObjectProperty relation,
			IndexedObjectSomeValuesFrom carry) {
		relation_ = relation;
		carry_ = carry;
	}

	@Override
	public String toString() {
		return "Propagation " + relation_ + "->" + carry_;
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// propagate over all backward links
		ruleAppVisitor.visit(ReflexivePropagationRule.getInstance(), this,
				premises, producer);
		ruleAppVisitor.visit(NonReflexivePropagationRule.getInstance(), this,
				premises, producer);
	}

	@Override
	public void applyNonRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		// propagate only over reflexive backward links
		ruleAppVisitor.visit(ReflexivePropagationRule.getInstance(), this,
				premises, producer);
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
	public IndexedObjectProperty getSubRoot() {
		return relation_;
	}

	/**
	 * @return the {@link IndexedObjectProperty} that is the relation over which
	 *         this {@link Propagation} is applied
	 */
	public IndexedObjectProperty getRelation() {
		return this.relation_;
	}

	/**
	 * @return the {@link IndexedObjectSomeValuesFrom} that is propagated by
	 *         this {@link Propagation}
	 */
	public IndexedObjectSomeValuesFrom getCarry() {
		return this.carry_;
	}

}
