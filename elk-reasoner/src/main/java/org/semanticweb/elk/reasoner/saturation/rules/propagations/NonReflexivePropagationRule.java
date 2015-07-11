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

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link NonReflexivePropagationRule} producing {@link Subsumer}s in the
 * source {@link Context}s of relevant non-reflexive {@link BackwardLink}s
 * stored in the {@link Context} with which this rule applies.
 * 
 * @see Context#getBackwardLinksByObjectProperty()
 * @see ReflexivePropagationRule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class NonReflexivePropagationRule extends AbstractPropagationRule {

	public static final String NAME = "Non-Reflexive Propagation";

	private static final NonReflexivePropagationRule INSTANCE_ = new NonReflexivePropagationRule();

	public static final NonReflexivePropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(Propagation premise, ContextPremises premises,
			ConclusionProducer producer) {
		final Map<IndexedObjectProperty, ? extends SubContextPremises> subContextMap = premises
				.getSubContextPremisesByObjectProperty();
		SubContextPremises targets = subContextMap.get(premise.getRelation());
		// IndexedClassExpression carry = premise.getCarry();
		for (IndexedContextRoot target : targets.getLinkedRoots()) {
			// producer.produce(target, new ComposedSubsumer(carry));
			producer.produce(new PropagatedSubsumer(premise, target));
		}
	}

	@Override
	public void accept(PropagationRuleVisitor visitor, Propagation premise,
			ContextPremises premises, ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
