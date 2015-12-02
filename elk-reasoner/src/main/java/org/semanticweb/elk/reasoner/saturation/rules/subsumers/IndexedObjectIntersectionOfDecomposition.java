package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A {@link SubsumerDecompositionRule} that processes an
 * {@link IndexedObjectIntersectionOf} and produces {@link SubClassInclusion}s for its
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
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(IndexedObjectIntersectionOf premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		producer.produce(new SubClassInclusionDecomposedFirstConjunct(premises.getRoot(),
				premise));
		producer.produce(new SubClassInclusionDecomposedSecondConjunct(premises.getRoot(),
				premise));
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor<?> visitor,
			IndexedObjectIntersectionOf premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);

	}

}
