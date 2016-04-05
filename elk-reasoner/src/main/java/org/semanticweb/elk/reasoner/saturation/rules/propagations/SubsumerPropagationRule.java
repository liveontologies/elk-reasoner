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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;

/**
 * A {@link PropagationRule} producing {@link SubClassInclusion}s in the source
 * {@link Context}s of relevant non-reflexive {@link BackwardLink}s stored in
 * the {@link ContextPremises} with which this rule applies.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerPropagationRule extends AbstractPropagationRule {

	public static final String NAME = "Propagation";

	private static final SubsumerPropagationRule INSTANCE_ = new SubsumerPropagationRule();

	public static final SubsumerPropagationRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(Propagation premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		final Map<IndexedObjectProperty, ? extends SubContextPremises> subContextMap = premises
				.getSubContextPremisesByObjectProperty();
		IndexedObjectProperty subRoot = premise.getSubDestination();
		SubContextPremises targets = subContextMap.get(subRoot);
		for (IndexedContextRoot target : targets.getLinkedRoots()) {
			producer.produce(new SubClassInclusionComposedObjectSomeValuesFrom(premise, target));
		}
		if (premises.getLocalReflexiveObjectProperties().contains(subRoot)) {
			producer.produce(new SubClassInclusionComposedObjectSomeValuesFrom(premise, premises.getRoot()));
		}
	}

	@Override
	public boolean isTracingRule() {
		return false;
	}

	@Override
	public void accept(PropagationRuleVisitor<?> visitor, Propagation premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
