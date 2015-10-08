/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerDecompositionVisitor;

/**
 * An implementation of {@link Subsumer}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <S>
 *            the type of the subsumer {@link IndexedClassExpression}
 */
public abstract class AbstractSubsumer<S extends IndexedClassExpression>
		extends AbstractConclusion implements Subsumer {

	/**
	 * the implied {@code IndexedClassExpression} represented by this
	 * {@link Subsumer}
	 */
	private final S expression_;

	public AbstractSubsumer(IndexedContextRoot root, S expression) {
		super(root);
		if (expression == null)
			throw new NullPointerException("Subsumer cannot be null!");
		this.expression_ = expression;
	}

	@Override
	public S getExpression() {
		return expression_;
	}

	void applyCompositionRules(RuleVisitor<?> ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		LinkedSubsumerRule compositionRule = expression_
				.getCompositionRuleHead();
		while (compositionRule != null) {
			compositionRule.accept(ruleAppVisitor, expression_, premises,
					producer);
			compositionRule = compositionRule.next();
		}
	}

	void applyDecompositionRules(RuleVisitor<?> ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		expression_.accept(new SubsumerDecompositionVisitor(ruleAppVisitor,
				premises, producer));
	}

}
