package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class SaturationConclusionBaseFactory
		implements
			SaturationConclusion.Factory {

	@SuppressWarnings("static-method")
	protected <C extends SaturationConclusion> C filter(C newConclusion) {
		// could be overridden in sub-classes
		return newConclusion;
	}

	@Override
	public ContextInitialization getContextInitialization(
			IndexedContextRoot root, OntologyIndex ontologyIndex) {
		return filter(new ContextInitializationImpl(root, ontologyIndex));
	}

	@Override
	public Contradiction getContradiction(IndexedContextRoot root) {
		return filter(new ContradictionImpl(root));
	}

	@Override
	public DisjointSubsumer getDisjointSubsumer(IndexedContextRoot root,
			IndexedClassExpressionList disjointExpressions, int position,
			ElkAxiom reason) {
		return filter(new DisjointSubsumerImpl(root, disjointExpressions,
				position, reason));
	}

	@Override
	public ForwardLink getForwardLink(IndexedContextRoot root,
			IndexedPropertyChain forwardChain, IndexedContextRoot target) {
		return filter(new ForwardLinkImpl<IndexedPropertyChain>(root,
				forwardChain, target));
	}

	@Override
	public BackwardLink getBackwardLink(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedContextRoot source) {
		return filter(new BackwardLinkImpl(root, relation, source));
	}

	@Override
	public Propagation getPropagation(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedObjectSomeValuesFrom carry) {
		return filter(new PropagationImpl(root, relation, carry));
	}

	@Override
	public SubContextInitialization getSubContextInitialization(
			IndexedContextRoot root, IndexedObjectProperty subRoot) {
		return filter(new SubContextInitializationImpl(root, subRoot));
	}

	@Override
	public SubClassInclusionComposed getComposedSubClassInclusion(
			IndexedContextRoot subExpression,
			IndexedClassExpression superExpression) {
		return filter(new SubClassInclusionComposedImpl<IndexedClassExpression>(
				subExpression, superExpression));
	}

	@Override
	public SubClassInclusionDecomposed getDecomposedSubClassInclusion(
			IndexedContextRoot subExpression,
			IndexedClassExpression superExpression) {
		return filter(new SubClassInclusionDecomposedImpl(subExpression,
				superExpression));
	}

	@Override
	public SubPropertyChain getSubPropertyChain(IndexedPropertyChain subChain,
			IndexedPropertyChain superChain) {
		return filter(new SubPropertyChainImpl(subChain, superChain));
	}

}
