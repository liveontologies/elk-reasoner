package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2024 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedOfDecomposed;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;

/**
 * 
 * A {@link SubsumerDecompositionRule} producing {@link SubClassInclusionComposed} when
 * processing a {@link SubClassInclusionDecomposed} for {@link IndexedClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ComposedFromDecomposedSubsumerRule extends
		AbstractSubsumerDecompositionRule<IndexedClassExpression> {

	private static final ComposedFromDecomposedSubsumerRule INSTANCE_ = new ComposedFromDecomposedSubsumerRule();

	public static final String NAME = "Composed from Decomposed Subsumer";

	@Override
	public String toString() {
		return NAME;
	}

	public static SubsumerDecompositionRule<IndexedClassExpression> getInstance() {
		return INSTANCE_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		producer.produce(new SubClassInclusionComposedOfDecomposed(premises.getRoot(), premise));
	}

	@Override
	public boolean isTracingRule() {
		return true;
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
