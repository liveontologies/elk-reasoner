package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;

/**
 * A {@link BackwardLinkRule} producing {@link SubClassInclusion}s when processing
 * {@link BackwardLink}s that are propagated over them using {@link Propagation}
 * s contained in the corresponding {@link SubContext}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerBackwardLinkRule extends AbstractBackwardLinkRule {

	public static final String NAME = "Propagation Over BackwardLink";

	private static final SubsumerBackwardLinkRule INSTANCE_ = new SubsumerBackwardLinkRule();

	public static SubsumerBackwardLinkRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {

		for (IndexedObjectSomeValuesFrom carry : premises
				.getPropagatedSubsumers(premise.getRelation())) {
			producer.produce(new SubClassInclusionComposedObjectSomeValuesFrom(premise, carry));
		}
	}

	@Override
	public boolean isTracingRule() {
		return true;
	}

	@Override
	public void accept(BackwardLinkRuleVisitor<?> visitor,
			BackwardLink premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
