package org.semanticweb.elk.matching.conclusions;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectDelegatingFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.ConclusionBaseFactory;

public class ConclusionMatchExpressionDelegatingFactory extends
		ElkObjectDelegatingFactory implements ConclusionMatchExpressionFactory {

	private final Conclusion.Factory conclusionFactory_;

	private final ConclusionMatch.Factory conclusionMatchFactory_;

	private final IndexedContextRootMatch.Factory rootMatchFactory_;

	public ConclusionMatchExpressionDelegatingFactory(
			ElkObject.Factory elkObjectFactory) {
		this(elkObjectFactory, new ConclusionBaseFactory(),
				new ConclusionMatchBaseFactory(),
				new IndexedContextRootMatchBaseFactory());
	}

	public ConclusionMatchExpressionDelegatingFactory(
			ElkObject.Factory elkObjectFactory,
			Conclusion.Factory conclusionFactory,
			ConclusionMatch.Factory conclusionMatchFactory,
			IndexedContextRootMatch.Factory rootMatchFactory) {
		super(elkObjectFactory);
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionMatchFactory_ = conclusionMatchFactory;
		this.rootMatchFactory_ = rootMatchFactory;
	}

	@SuppressWarnings("static-method")
	protected <C extends Conclusion> C filter(C candidate) {
		// could be overridden in sub-classes
		return candidate;
	}

	@SuppressWarnings("static-method")
	protected <C extends ConclusionMatch> C filter(C candidate) {
		// could be overridden in sub-classes
		return candidate;
	}

	@SuppressWarnings("static-method")
	protected <C extends IndexedContextRootMatch> C filter(C candidate) {
		// could be overridden in sub-classes
		return candidate;
	}

	@Override
	public BackwardLink getBackwardLink(IndexedContextRoot destination,
			IndexedObjectProperty relation, IndexedContextRoot source) {
		return filter(conclusionFactory_.getBackwardLink(destination, relation,
				source));
	}

	@Override
	public BackwardLinkMatch1 getBackwardLinkMatch1(BackwardLink parent,
			IndexedContextRootMatch sourceMatch,
			ElkObjectProperty relationMatch) {
		return filter(conclusionMatchFactory_.getBackwardLinkMatch1(parent,
				sourceMatch, relationMatch));
	}

	@Override
	public BackwardLinkMatch2 getBackwardLinkMatch2(BackwardLinkMatch1 parent,
			IndexedContextRootMatch destinationMatch) {
		return filter(conclusionMatchFactory_.getBackwardLinkMatch2(parent,
				destinationMatch));
	}

	@Override
	public ContextInitialization getContextInitialization(
			IndexedContextRoot root) {
		return filter(conclusionFactory_.getContextInitialization(root));
	}

	@Override
	public ClassInconsistency getContradiction(IndexedContextRoot destination) {
		return filter(conclusionFactory_.getContradiction(destination));
	}

	@Override
	public DisjointSubsumer getDisjointSubsumer(IndexedContextRoot root,
			IndexedClassExpressionList disjointExpressions, int position,
			ElkAxiom reason) {
		return filter(conclusionFactory_.getDisjointSubsumer(root,
				disjointExpressions, position, reason));
	}

	@Override
	public ForwardLink getForwardLink(IndexedContextRoot destination,
			IndexedPropertyChain relation, IndexedContextRoot target) {
		return filter(conclusionFactory_.getForwardLink(destination, relation,
				target));
	}

	@Override
	public ForwardLinkMatch1 getForwardLinkMatch1(ForwardLink parent,
			IndexedContextRootMatch destinationMatch,
			ElkSubObjectPropertyExpression fullForwardChainMatch,
			int forwardChainStartPos) {
		return filter(conclusionMatchFactory_.getForwardLinkMatch1(parent,
				destinationMatch, fullForwardChainMatch, forwardChainStartPos));
	}

	@Override
	public ForwardLinkMatch2 getForwardLinkMatch2(ForwardLinkMatch1 parent,
			IndexedContextRootMatchChain intermediateRoots,
			IndexedContextRootMatch targetMatch) {
		return filter(conclusionMatchFactory_.getForwardLinkMatch2(parent,
				intermediateRoots, targetMatch));
	}

	@Override
	public IndexedClassExpressionMatch getIndexedClassExpressionMatch(
			ElkClassExpression match) {
		return filter(rootMatchFactory_.getIndexedClassExpressionMatch(match));
	}

	@Override
	public IndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ElkAxiom originalAxiom, IndexedEntity entity) {
		return filter(conclusionFactory_
				.getIndexedDeclarationAxiom(originalAxiom, entity));
	}

	@Override
	public IndexedEquivalentClassesAxiom getIndexedEquivalentClassesAxiom(
			ElkAxiom originalAxiom, IndexedClassExpression firstMember,
			IndexedClassExpression secondMember) {
		return filter(conclusionFactory_.getIndexedEquivalentClassesAxiom(
				originalAxiom, firstMember, secondMember));
	}

	@Override
	public IndexedEquivalentClassesAxiomMatch1 getIndexedEquivalentClassesAxiomMatch1(
			IndexedEquivalentClassesAxiom parent) {
		return filter(conclusionMatchFactory_
				.getIndexedEquivalentClassesAxiomMatch1(parent));
	}

	@Override
	public IndexedEquivalentClassesAxiomMatch2 getIndexedEquivalentClassesAxiomMatch2(
			IndexedEquivalentClassesAxiomMatch1 parent, ElkClassExpression firstMemberMatch,
			ElkClassExpression secondMemberMatch) {
		return filter(conclusionMatchFactory_.getIndexedEquivalentClassesAxiomMatch2(
				parent, firstMemberMatch, secondMemberMatch));
	}

	@Override
	public IndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom, IndexedClassExpressionList members) {
		return filter(conclusionFactory_
				.getIndexedDisjointClassesAxiom(originalAxiom, members));
	}

	@Override
	public IndexedDisjointClassesAxiomMatch1 getIndexedDisjointClassesAxiomMatch1(
			IndexedDisjointClassesAxiom parent) {
		return filter(conclusionMatchFactory_
				.getIndexedDisjointClassesAxiomMatch1(parent));
	}

	@Override
	public IndexedDisjointClassesAxiomMatch2 getIndexedDisjointClassesAxiomMatch2(
			IndexedDisjointClassesAxiomMatch1 parent,
			List<? extends ElkClassExpression> memberMatches) {
		return filter(conclusionMatchFactory_
				.getIndexedDisjointClassesAxiomMatch2(parent, memberMatches));
	}

	@Override
	public IndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ElkAxiom originalAxiom, IndexedObjectProperty property,
			IndexedClassExpression range) {
		return filter(conclusionFactory_.getIndexedObjectPropertyRangeAxiom(
				originalAxiom, property, range));
	}

	@Override
	public IndexedObjectPropertyRangeAxiomMatch1 getIndexedObjectPropertyRangeAxiomMatch1(
			IndexedObjectPropertyRangeAxiom parent) {
		return filter(conclusionMatchFactory_
				.getIndexedObjectPropertyRangeAxiomMatch1(parent));
	}

	@Override
	public IndexedObjectPropertyRangeAxiomMatch2 getIndexedObjectPropertyRangeAxiomMatch2(
			IndexedObjectPropertyRangeAxiomMatch1 parent,
			ElkObjectProperty propertyMatch, ElkClassExpression rangeMatch) {
		return filter(conclusionMatchFactory_
				.getIndexedObjectPropertyRangeAxiomMatch2(parent, propertyMatch,
						rangeMatch));
	}

	@Override
	public IndexedRangeFillerMatch getIndexedRangeFillerMatch(
			ElkObjectSomeValuesFrom existentialMatch) {
		return filter(
				rootMatchFactory_.getIndexedRangeFillerMatch(existentialMatch));
	}

	@Override
	public IndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ElkAxiom originalAxiom, IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		return filter(conclusionFactory_.getIndexedSubClassOfAxiom(
				originalAxiom, subClass, superClass));
	}

	@Override
	public IndexedSubClassOfAxiomMatch1 getIndexedSubClassOfAxiomMatch1(
			IndexedSubClassOfAxiom parent) {
		return filter(conclusionMatchFactory_
				.getIndexedSubClassOfAxiomMatch1(parent));
	}

	@Override
	public IndexedSubClassOfAxiomMatch2 getIndexedSubClassOfAxiomMatch2(
			IndexedSubClassOfAxiomMatch1 parent,
			ElkClassExpression subClassMatch,
			ElkClassExpression superClassMatch) {
		return filter(conclusionMatchFactory_.getIndexedSubClassOfAxiomMatch2(
				parent, subClassMatch, superClassMatch));
	}

	@Override
	public IndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom, IndexedPropertyChain subPropertyChain,
			IndexedObjectProperty superProperty) {
		return filter(conclusionFactory_.getIndexedSubObjectPropertyOfAxiom(
				originalAxiom, subPropertyChain, superProperty));
	}

	@Override
	public IndexedSubObjectPropertyOfAxiomMatch1 getIndexedSubObjectPropertyOfAxiomMatch1(
			IndexedSubObjectPropertyOfAxiom parent) {
		return filter(conclusionMatchFactory_
				.getIndexedSubObjectPropertyOfAxiomMatch1(parent));
	}

	@Override
	public IndexedSubObjectPropertyOfAxiomMatch2 getIndexedSubObjectPropertyOfAxiomMatch2(
			IndexedSubObjectPropertyOfAxiomMatch1 parent,
			ElkSubObjectPropertyExpression subPropertyChainMatch,
			ElkObjectProperty superPropertyMatch) {
		return filter(conclusionMatchFactory_
				.getIndexedSubObjectPropertyOfAxiomMatch2(parent,
						subPropertyChainMatch, superPropertyMatch));
	}

	@Override
	public Propagation getPropagation(IndexedContextRoot destination,
			IndexedObjectProperty relation, IndexedObjectSomeValuesFrom carry) {
		return filter(conclusionFactory_.getPropagation(destination, relation,
				carry));
	}

	@Override
	public PropagationMatch1 getPropagationMatch1(Propagation parent,
			ElkObjectSomeValuesFrom carryMatch) {
		return filter(conclusionMatchFactory_.getPropagationMatch1(parent,
				carryMatch));
	}

	@Override
	public PropagationMatch2 getPropagationMatch2(PropagationMatch1 parent,
			ElkObjectProperty relationMatch) {
		return filter(conclusionMatchFactory_.getPropagationMatch2(parent,
				relationMatch));
	}

	@Override
	public PropagationMatch3 getPropagationMatch3(PropagationMatch2 parent,
			IndexedContextRootMatch destinationMatch) {
		return filter(conclusionMatchFactory_.getPropagationMatch3(parent,
				destinationMatch));
	}

	@Override
	public PropertyRange getPropertyRange(IndexedObjectProperty property,
			IndexedClassExpression range) {
		return filter(conclusionFactory_.getPropertyRange(property, range));
	}

	@Override
	public PropertyRangeMatch1 getPropertyRangeMatch1(PropertyRange parent,
			ElkObjectProperty propertyMatch) {
		return filter(conclusionMatchFactory_.getPropertyRangeMatch1(parent,
				propertyMatch));
	}

	@Override
	public PropertyRangeMatch2 getPropertyRangeMatch2(
			PropertyRangeMatch1 parent, ElkClassExpression rangeMatch) {
		return filter(conclusionMatchFactory_.getPropertyRangeMatch2(parent,
				rangeMatch));
	}

	@Override
	public SubClassInclusionComposed getSubClassInclusionComposed(
			IndexedContextRoot destination, IndexedClassExpression subsumer) {
		return filter(conclusionFactory_
				.getSubClassInclusionComposed(destination, subsumer));
	}

	@Override
	public SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkClassExpression subsumerMatch) {
		return filter(
				conclusionMatchFactory_.getSubClassInclusionComposedMatch1(
						parent, destinationMatch, subsumerMatch));
	}

	@Override
	public SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkObjectIntersectionOf fullSubsumerMatch,
			int subsumerPrefixLength) {
		return filter(conclusionMatchFactory_
				.getSubClassInclusionComposedMatch1(parent, destinationMatch,
						fullSubsumerMatch, subsumerPrefixLength));
	}

	@Override
	public SubClassInclusionDecomposed getSubClassInclusionDecomposed(
			IndexedContextRoot destination, IndexedClassExpression subsumer) {
		return filter(conclusionFactory_
				.getSubClassInclusionDecomposed(destination, subsumer));
	}

	@Override
	public SubClassInclusionDecomposedMatch1 getSubClassInclusionDecomposedMatch1(
			SubClassInclusionDecomposed parent,
			IndexedContextRootMatch destinationMatch) {
		return filter(
				conclusionMatchFactory_.getSubClassInclusionDecomposedMatch1(
						parent, destinationMatch));
	}

	@Override
	public SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
			SubClassInclusionDecomposedMatch1 parent,
			ElkClassExpression subsumerMatch) {
		return filter(conclusionMatchFactory_
				.getSubClassInclusionDecomposedMatch2(parent, subsumerMatch));
	}

	@Override
	public SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
			SubClassInclusionDecomposedMatch1 parent,
			ElkObjectIntersectionOf subsumerFullConjunctionMatch,
			int subsumerConjunctionPrefixLength) {
		return filter(
				conclusionMatchFactory_.getSubClassInclusionDecomposedMatch2(
						parent, subsumerFullConjunctionMatch,
						subsumerConjunctionPrefixLength));
	}

	@Override
	public SubContextInitialization getSubContextInitialization(
			IndexedContextRoot root, IndexedObjectProperty subRoot) {
		return filter(
				conclusionFactory_.getSubContextInitialization(root, subRoot));
	}

	@Override
	public SubPropertyChain getSubPropertyChain(IndexedPropertyChain subChain,
			IndexedPropertyChain superChain) {
		return filter(
				conclusionFactory_.getSubPropertyChain(subChain, superChain));
	}

	@Override
	public SubPropertyChainMatch1 getSubPropertyChainMatch1(
			SubPropertyChain parent,
			ElkSubObjectPropertyExpression fullSuperChainMatch,
			int superChainStartPos) {
		return filter(conclusionMatchFactory_.getSubPropertyChainMatch1(parent,
				fullSuperChainMatch, superChainStartPos));
	}

	@Override
	public SubPropertyChainMatch2 getSubPropertyChainMatch2(
			SubPropertyChainMatch1 parent,
			ElkSubObjectPropertyExpression fullSubChainMatch,
			int subChainStartPos) {
		return filter(conclusionMatchFactory_.getSubPropertyChainMatch2(parent,
				fullSubChainMatch, subChainStartPos));
	}

}
