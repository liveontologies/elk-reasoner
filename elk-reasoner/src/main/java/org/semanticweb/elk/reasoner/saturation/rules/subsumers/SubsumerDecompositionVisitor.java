package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.model.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPredefinedClass;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;

/**
 * An {@link IndexedClassExpression.Visitor} applying decomposition rules using a
 * given {@link SubsumerDecompositionRuleVisitor} using given
 * {@link ContextPremises} and producing conclusions using a given
 * {@link ClassInferenceProducer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerDecompositionVisitor implements
		IndexedClassExpression.Visitor<Void> {

	/**
	 * the rule visitor used when applying decomposition rules
	 */
	private final SubsumerDecompositionRuleVisitor<?> ruleVisitor_;

	/**
	 * the {@link ContextPremises} with which the rules are applied
	 */
	private final ContextPremises premises_;

	/**
	 * the producer for conclusions
	 */
	private final ClassInferenceProducer producer_;

	public SubsumerDecompositionVisitor(
			SubsumerDecompositionRuleVisitor<?> ruleVisitor,
			ContextPremises premises, ClassInferenceProducer producer) {
		this.ruleVisitor_ = ruleVisitor;
		this.premises_ = premises;
		this.producer_ = producer;
	}

	@Override
	public Void visit(IndexedDefinedClass element) {
		IndexedClassDecompositionRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedPredefinedClass element) {
		OwlNothingDecompositionRule.getInstance().accept(ruleVisitor_, element,
				premises_, producer_);
		return null;
	}
	
	
	@Override
	public Void visit(IndexedIndividual element) {
		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectComplementOf element) {
		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		IndexedObjectComplementOfDecomposition.getInstance().accept(
				ruleVisitor_, element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectIntersectionOf element) {
		IndexedObjectIntersectionOfDecomposition.getInstance().accept(
				ruleVisitor_, element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectSomeValuesFrom element) {
		IndexedObjectSomeValuesFromDecomposition.getInstance().accept(
				ruleVisitor_, element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectHasSelf element) {
		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		IndexedObjectHasSelfDecomposition.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedObjectUnionOf element) {
		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		return null;
	}

	@Override
	public Void visit(IndexedDataHasValue element) {
		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleVisitor_,
				element, premises_, producer_);
		return null;
	}

}
