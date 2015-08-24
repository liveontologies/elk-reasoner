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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * An {@link IndexedClassExpressionVisitor} applying decomposition rules using a
 * given {@link SubsumerDecompositionRuleVisitor} using given
 * {@link ContextPremises} and producing conclusions using a given
 * {@link ConclusionProducer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumerDecompositionVisitor implements
		IndexedClassExpressionVisitor<Void> {

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
	private final ConclusionProducer producer_;

	public SubsumerDecompositionVisitor(
			SubsumerDecompositionRuleVisitor<?> ruleVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		this.ruleVisitor_ = ruleVisitor;
		this.premises_ = premises;
		this.producer_ = producer;
	}

	@Override
	public Void visit(IndexedClass element) {
		// no rules are applicable
		return null;
	}

	@Override
	public Void visit(IndexedIndividual element) {
		// no rules are applicable
		return null;
	}

	@Override
	public Void visit(IndexedObjectComplementOf element) {
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
	public Void visit(IndexedObjectUnionOf element) {
		// not supported
		return null;
	}

	@Override
	public Void visit(IndexedDataHasValue element) {
		// not supported
		return null;
	}

}
