package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromDisjointSubsumers;

/**
 * A {@link DisjointSubsumerRule} applied when processing a
 * {@link DisjointSubsumer} producing {@link Contradiction} caused by violation
 * of disjointness constrains of this {@link DisjointSubsumer}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionCompositionRule extends AbstractDisjointSubsumerRule {

	public static final String NAME = "Contradiction by Disjointness Axiom";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(DisjointSubsumer premise, ContextPremises premises,
			ConclusionProducer producer) {
		IndexedDisjointClassesAxiom axiom = premise.getAxiom();
		IndexedClassExpression[] disjointSubsumers = premises
				.getDisjointSubsumers(axiom); // should not be null

		if (disjointSubsumers[1] != null) {
			// two disjoint members were derived
			producer.produce(new ContradictionFromDisjointSubsumers(premise,
					disjointSubsumers));
		}
	}

	@Override
	public void accept(DisjointSubsumerRuleVisitor visitor,
			DisjointSubsumer premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}