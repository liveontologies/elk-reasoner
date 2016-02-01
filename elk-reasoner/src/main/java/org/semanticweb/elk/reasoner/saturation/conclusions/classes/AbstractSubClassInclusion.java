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
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerDecompositionVisitor;

/**
 * An implementation of {@link SubClassInclusion}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <S>
 *            the type of the super-expression of {@link IndexedClassExpression}
 */
public abstract class AbstractSubClassInclusion<S extends IndexedClassExpression>
		extends AbstractClassConclusion implements SubClassInclusion {

	/**
	 * the implied {@code IndexedClassExpression} represented by this
	 * {@link SubClassInclusion}
	 */
	private final S expression_;

	protected AbstractSubClassInclusion(IndexedContextRoot subExpression,
			S superExpression) {
		super(subExpression);
		if (superExpression == null)
			throw new NullPointerException("Subsumer cannot be null!");
		this.expression_ = superExpression;
	}

	@Override
	public IndexedContextRoot getSubExpression() {
		return getDestination();
	}
	
	@Override
	public S getSuperExpression() {
		return expression_;
	}

	void applyCompositionRules(RuleVisitor<?> ruleAppVisitor,
			ContextPremises premises, ClassInferenceProducer producer) {
		LinkedSubsumerRule compositionRule = expression_
				.getCompositionRuleHead();
		while (compositionRule != null) {
			compositionRule.accept(ruleAppVisitor, expression_, premises,
					producer);
			compositionRule = compositionRule.next();
		}
	}

	void applyDecompositionRules(RuleVisitor<?> ruleAppVisitor,
			ContextPremises premises, ClassInferenceProducer producer) {
		expression_.accept(new SubsumerDecompositionVisitor(ruleAppVisitor,
				premises, producer));
	}
	
	@Override
	public <O> O accept(ClassConclusion.Visitor<O> visitor) {
		return accept((SubClassInclusion.Visitor<O>) visitor);
	}

}
