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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link BackwardLinkRule} applied when processing {@link BackwardLink}
 * producing new {@link Propagation}s that can be used with this
 * {@link BackwardLink}
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationFromBackwardLinkRule extends AbstractBackwardLinkRule {

	private static final String NAME_ = "Propagations For BackwardLink";

	private static final PropagationFromBackwardLinkRule INSTANCE_ = new PropagationFromBackwardLinkRule();

	public static PropagationFromBackwardLinkRule getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(BackwardLink premise, ContextPremises premises,
			ConclusionProducer producer) {
		IndexedPropertyChain premiseRelation = premise.getRelation();
		// if this is the first/last backward link for this relation,
		// generate new propagations for this relation
		if (premises.getBackwardLinksByObjectProperty().get(premiseRelation)
				.size() == 1) {
			IndexedObjectSomeValuesFrom.generatePropagations(premiseRelation,
					premises, producer);
		}
	}

	@Override
	public void accept(BackwardLinkRuleVisitor visitor, BackwardLink premise,
			ContextPremises premises, ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}