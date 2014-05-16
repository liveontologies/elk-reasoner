package org.semanticweb.elk.reasoner.saturation.rules.propagations;

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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;

/**
 * A {@link PropagationRule} producing {@link Subsumer}s in the {@link Context}s
 * in which the rule applies propagated over reflexive backward links stored in
 * this {@link Context}
 * 
 * @see Context#getLocalReflexiveObjectProperties()
 * @see NonReflexivePropagationRule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ReflexivePropagationRule extends AbstractPropagationRule {

	public static final String NAME = "Reflexive Propagation";

	private static final ReflexivePropagationRule INSTANCE_ = new ReflexivePropagationRule();

	public static final ReflexivePropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(Propagation premise, ContextPremises premises,
			ConclusionProducer producer) {
		final Set<IndexedObjectProperty> reflexive = premises
				.getLocalReflexiveObjectProperties();
		if (reflexive.contains(premise.getRelation())) {
			// producer.produce(premises.getRoot(), new
			// ComposedSubsumer(premise.getCarry()));
			IndexedClassExpression thisRoot = premises.getRoot();

			producer.produce(thisRoot, new PropagatedSubsumer(thisRoot,
					premise, premise.getRelation(), thisRoot));
		}
	}

	@Override
	public void accept(PropagationRuleVisitor visitor, Propagation premise,
			ContextPremises premises, ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
