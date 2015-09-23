package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * 
 * A {@link SubsumerDecompositionRule} producing {@link ComposedSubsumer} when
 * processing a {@link DecomposedSubsumer} for {@link IndexedClassEntity}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ComposedFromDecomposedSubsumerRule extends
		AbstractSubsumerDecompositionRule<IndexedClassEntity> {

	private static final ComposedFromDecomposedSubsumerRule INSTANCE_ = new ComposedFromDecomposedSubsumerRule();

	public static final String NAME = "Composed from Decomposed Subsumer";

	@Override
	public String getName() {
		return NAME;
	}

	public static SubsumerDecompositionRule<IndexedClassEntity> getInstance() {
		return INSTANCE_;
	}

	@Override
	public void apply(IndexedClassEntity premise, ContextPremises premises,
			ConclusionProducer producer) {
		producer.produce(new ComposedDecomposition(premises.getRoot(), premise));
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor<?> visitor,
			IndexedClassEntity premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
