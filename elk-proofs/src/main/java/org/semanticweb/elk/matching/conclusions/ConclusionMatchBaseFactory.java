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

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.root.IndexedContextRootMatchChain;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class ConclusionMatchBaseFactory implements ConclusionMatch.Factory {

	@Override
	public BackwardLinkMatch1 getBackwardLinkMatch1(BackwardLink parent,
			IndexedContextRootMatch sourceMatch) {
		return new BackwardLinkMatch1(parent, sourceMatch);
	}

	@Override
	public BackwardLinkMatch2 getBackwardLinkMatch2(BackwardLinkMatch1 parent,
			ElkObjectProperty relationMatch,
			IndexedContextRootMatch destinationMatch) {
		return new BackwardLinkMatch2(parent, relationMatch, destinationMatch);
	}

	@Override
	public BackwardLinkMatch3 getBackwardLinkMatch3(BackwardLinkMatch2 parent,
			IndexedContextRootMatch extendedDestinationMatch) {
		return new BackwardLinkMatch3(parent, extendedDestinationMatch);
	}

	@Override
	public BackwardLinkMatch4 getBackwardLinkMatch4(BackwardLinkMatch3 parent,
			IndexedContextRootMatch extendedSourceMatch) {
		return new BackwardLinkMatch4(parent, extendedSourceMatch);
	}

	@Override
	public ClassInconsistencyMatch1 getClassInconsistencyMatch1(
			ClassInconsistency parent,
			IndexedContextRootMatch destinationMatch) {
		return new ClassInconsistencyMatch1(parent, destinationMatch);
	}

	@Override
	public ClassInconsistencyMatch2 getClassInconsistencyMatch2(
			ClassInconsistencyMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch) {
		return new ClassInconsistencyMatch2(parent, extendedDestinationMatch);
	}

	@Override
	public DisjointSubsumerMatch1 getDisjointSubsumerMatch1(
			DisjointSubsumer parent, IndexedContextRootMatch destinationMatch) {
		return new DisjointSubsumerMatch1(parent, destinationMatch);
	}

	@Override
	public DisjointSubsumerMatch2 getDisjointSubsumerMatch2(
			DisjointSubsumerMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch,
			List<? extends ElkClassExpression> disjointExpressionsMatch) {
		return new DisjointSubsumerMatch2(parent, extendedDestinationMatch,
				disjointExpressionsMatch);
	}

	@Override
	public ForwardLinkMatch1 getForwardLinkMatch1(ForwardLink parent,
			IndexedContextRootMatch destinationMatch,
			ElkSubObjectPropertyExpression fullForwardChainMatch,
			int forwardChainStartPos) {
		return new ForwardLinkMatch1(parent, destinationMatch,
				fullForwardChainMatch, forwardChainStartPos);
	}

	@Override
	public ForwardLinkMatch2 getForwardLinkMatch2(ForwardLinkMatch1 parent,
			IndexedContextRootMatch targetMatch) {
		return new ForwardLinkMatch2(parent, targetMatch);
	}

	@Override
	public ForwardLinkMatch3 getForwardLinkMatch3(ForwardLinkMatch2 parent,
			IndexedContextRootMatch extendedTargetMatch) {
		return new ForwardLinkMatch3(parent, extendedTargetMatch);
	}

	@Override
	public ForwardLinkMatch4 getForwardLinkMatch4(ForwardLinkMatch3 parent,
			IndexedContextRootMatchChain extendedDomains) {
		return new ForwardLinkMatch4(parent, extendedDomains);
	}

	@Override
	public IndexedDisjointClassesAxiomMatch1 getIndexedDisjointClassesAxiomMatch1(
			IndexedDisjointClassesAxiom parent) {
		return new IndexedDisjointClassesAxiomMatch1(parent);
	}

	@Override
	public IndexedDisjointClassesAxiomMatch2 getIndexedDisjointClassesAxiomMatch2(
			IndexedDisjointClassesAxiomMatch1 parent,
			List<? extends ElkClassExpression> memberMatches) {
		return new IndexedDisjointClassesAxiomMatch2(parent, memberMatches);
	}

	@Override
	public IndexedEquivalentClassesAxiomMatch1 getIndexedEquivalentClassesAxiomMatch1(
			IndexedEquivalentClassesAxiom parent) {
		return new IndexedEquivalentClassesAxiomMatch1(parent);
	}

	@Override
	public IndexedEquivalentClassesAxiomMatch2 getIndexedEquivalentClassesAxiomMatch2(
			IndexedEquivalentClassesAxiomMatch1 parent,
			ElkClassExpression firstMemberMatch,
			ElkClassExpression secondMemberMatch) {
		return new IndexedEquivalentClassesAxiomMatch2(parent, firstMemberMatch,
				secondMemberMatch);
	}

	@Override
	public IndexedObjectPropertyRangeAxiomMatch1 getIndexedObjectPropertyRangeAxiomMatch1(
			IndexedObjectPropertyRangeAxiom parent) {
		return new IndexedObjectPropertyRangeAxiomMatch1(parent);
	}

	@Override
	public IndexedObjectPropertyRangeAxiomMatch2 getIndexedObjectPropertyRangeAxiomMatch2(
			IndexedObjectPropertyRangeAxiomMatch1 parent,
			ElkObjectProperty propertyMatch, ElkClassExpression rangeMatch) {
		return new IndexedObjectPropertyRangeAxiomMatch2(parent, propertyMatch,
				rangeMatch);
	}

	@Override
	public IndexedSubClassOfAxiomMatch1 getIndexedSubClassOfAxiomMatch1(
			IndexedSubClassOfAxiom parent) {
		return new IndexedSubClassOfAxiomMatch1(parent);
	}

	@Override
	public IndexedSubClassOfAxiomMatch2 getIndexedSubClassOfAxiomMatch2(
			IndexedSubClassOfAxiomMatch1 parent,
			ElkClassExpression subClassMatch,
			ElkClassExpression superClassMatch) {
		return new IndexedSubClassOfAxiomMatch2(parent, subClassMatch,
				superClassMatch);
	}

	@Override
	public IndexedSubObjectPropertyOfAxiomMatch1 getIndexedSubObjectPropertyOfAxiomMatch1(
			IndexedSubObjectPropertyOfAxiom parent) {
		return new IndexedSubObjectPropertyOfAxiomMatch1(parent);
	}

	@Override
	public IndexedSubObjectPropertyOfAxiomMatch2 getIndexedSubObjectPropertyOfAxiomMatch2(
			IndexedSubObjectPropertyOfAxiomMatch1 parent,
			ElkSubObjectPropertyExpression subPropertyChainMatch,
			ElkObjectProperty superPropertyMatch) {
		return new IndexedSubObjectPropertyOfAxiomMatch2(parent,
				subPropertyChainMatch, superPropertyMatch);
	}

	@Override
	public PropagationMatch1 getPropagationMatch1(Propagation parent,
			IndexedContextRootMatch destinationMatch,
			ElkObjectProperty subDestinationMatch,
			IndexedObjectSomeValuesFromMatch carryMatch) {
		return new PropagationMatch1(parent, destinationMatch,
				subDestinationMatch, carryMatch);
	}

	@Override
	public PropagationMatch2 getPropagationMatch2(PropagationMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch) {
		return new PropagationMatch2(parent, extendedDestinationMatch);
	}

	@Override
	public PropertyRangeMatch1 getPropertyRangeMatch1(PropertyRange parent) {
		return new PropertyRangeMatch1(parent);
	}

	@Override
	public PropertyRangeMatch2 getPropertyRangeMatch2(
			PropertyRangeMatch1 parent, ElkObjectProperty propertyMatch,
			ElkClassExpression rangeMatch) {
		return new PropertyRangeMatch2(parent, propertyMatch, rangeMatch);
	}

	@Override
	public SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkClassExpression subsumerMatchValue) {
		return new SubClassInclusionComposedMatch1(parent, destinationMatch,
				subsumerMatchValue);
	}

	@Override
	public SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkIndividual subsumerMatchValue) {
		return new SubClassInclusionComposedMatch1(parent, destinationMatch,
				subsumerMatchValue);
	}

	@Override
	public SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkObjectIntersectionOf fullSubsumerMatch,
			int subsumerPrefixLength) {
		return new SubClassInclusionComposedMatch1(parent, destinationMatch,
				fullSubsumerMatch, subsumerPrefixLength);
	}

	@Override
	public SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			SubsumerMatch subsumerMatch) {
		return new SubClassInclusionComposedMatch1(parent, destinationMatch,
				subsumerMatch);
	}

	@Override
	public SubClassInclusionComposedMatch2 getSubClassInclusionComposedMatch2(
			SubClassInclusionComposedMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch) {
		return new SubClassInclusionComposedMatch2(parent,
				extendedDestinationMatch);
	}

	@Override
	public SubClassInclusionDecomposedMatch1 getSubClassInclusionDecomposedMatch1(
			SubClassInclusionDecomposed parent,
			IndexedContextRootMatch destinationMatch) {
		return new SubClassInclusionDecomposedMatch1(parent, destinationMatch);
	}

	@Override
	public SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
			SubClassInclusionDecomposedMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch,
			ElkClassExpression subsumerMatch) {
		return new SubClassInclusionDecomposedMatch2(parent,
				extendedDestinationMatch, subsumerMatch);
	}

	@Override
	public SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
			SubClassInclusionDecomposedMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch,
			ElkIndividual subsumerMatchValue) {
		return new SubClassInclusionDecomposedMatch2(parent,
				extendedDestinationMatch, subsumerMatchValue);
	}

	@Override
	public SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
			SubClassInclusionDecomposedMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch,
			ElkObjectIntersectionOf subsumerFullConjunctionMatch,
			int subsumerConjunctionPrefixLength) {
		return new SubClassInclusionDecomposedMatch2(parent,
				extendedDestinationMatch, subsumerFullConjunctionMatch,
				subsumerConjunctionPrefixLength);
	}

	@Override
	public SubClassInclusionDecomposedMatch2 getSubClassInclusionDecomposedMatch2(
			SubClassInclusionDecomposedMatch1 parent,
			IndexedContextRootMatch extendedDestinationMatch,
			SubsumerMatch subsumerMatch) {
		return new SubClassInclusionDecomposedMatch2(parent,
				extendedDestinationMatch, subsumerMatch);
	}

	@Override
	public SubPropertyChainMatch1 getSubPropertyChainMatch1(
			SubPropertyChain parent,
			ElkSubObjectPropertyExpression fullSuperChainMatch,
			int superChainStartPos) {
		return new SubPropertyChainMatch1(parent, fullSuperChainMatch,
				superChainStartPos);
	}

	@Override
	public SubPropertyChainMatch2 getSubPropertyChainMatch2(
			SubPropertyChainMatch1 parent,
			ElkSubObjectPropertyExpression fullSubChainMatch,
			int subChainStartPos) {
		return new SubPropertyChainMatch2(parent, fullSubChainMatch,
				subChainStartPos);
	}

}
