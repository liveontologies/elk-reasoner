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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;

/**
 * A {@link BackwardLinkRule} producing {@link Subsumer}s when processing
 * {@link BackwardLink}s that are propagated over them using {@link Propagation}
 * s contained in the corresponding {@link SubContext}
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
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedObjectSomeValuesFrom carry : premises
				.getSubContextPremisesByObjectProperty()
				.get(premise.getRelation()).getPropagatedSubsumers()) {
			//producer.produce(premise.getSource(), new ComposedSubsumer(carry));
			producer.produce(premise.getSource(), new PropagatedSubsumer(premises.getRoot(), premise, carry));
		}
	}

	@Override
	public void accept(BackwardLinkRuleVisitor visitor, BackwardLink premise,
			ContextPremises premises, ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
