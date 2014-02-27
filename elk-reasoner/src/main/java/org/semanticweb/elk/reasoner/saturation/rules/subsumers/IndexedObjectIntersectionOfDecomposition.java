package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;

/**
 * A {@link SubsumerDecompositionRule} that processes an
 * {@link IndexedObjectIntersectionOf} and produces {@link Subsumer}s for its
 * conjuncts
 * 
 * @see IndexedObjectIntersectionOf#getFirstConjunct()
 * @see IndexedObjectIntersectionOf#getSecondConjunct()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectIntersectionOfDecomposition extends
		AbstractSubsumerDecompositionRule<IndexedObjectIntersectionOf> {

	public static final String NAME = "ObjectIntersectionOf Decomposition";

	private static IndexedObjectIntersectionOfDecomposition INSTANCE_ = new IndexedObjectIntersectionOfDecomposition();

	public static IndexedObjectIntersectionOfDecomposition getInstance() {
		return INSTANCE_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(IndexedObjectIntersectionOf premise,
			ContextPremises premises, ConclusionProducer producer) {
		//producer.produce(premises.getRoot(), new DecomposedSubsumer(premise.getFirstConjunct()));
		//producer.produce(premises.getRoot(), new DecomposedSubsumer(premise.getSecondConjunct()));
		
		producer.produce(premises.getRoot(), new DecomposedConjunction(premise, premise.getFirstConjunct()));
		producer.produce(premises.getRoot(), new DecomposedConjunction(premise, premise.getSecondConjunct()));
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor visitor,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);

	}

}
