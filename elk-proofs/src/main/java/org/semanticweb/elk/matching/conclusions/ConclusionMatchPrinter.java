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

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class ConclusionMatchPrinter implements ConclusionMatch.Visitor<String> {

	private static ConclusionMatchPrinter INSTANCE_ = new ConclusionMatchPrinter();

	private static String MATCH_LEVEL_ = " | ", // separator of match levels
			MATCH_SEP_ = ", ", // separator of matches within level
			MATCH_ = " = ";// match symbol

	private ConclusionMatchPrinter() {

	}

	public static String toString(ConclusionMatch match) {
		return match.accept(INSTANCE_);
	}

	public static String toString(IndexedContextRootMatchChain chain) {
		if (chain == null) {
			return "";
		}
		// else
		String suffix = toString(chain.getTail());
		return chain.getHead() + (suffix.isEmpty() ? "" : "∘") + suffix;
	}

	static ConclusionMatch.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	private String chainMatch(ElkSubObjectPropertyExpression chain,
			int startPos) {
		return chain + "[" + (startPos + 1) + "-]";
	}

	@Override
	public String visit(IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		return conclusionMatch.getParent() + MATCH_LEVEL_;
	}

	@Override
	public String visit(IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		IndexedDisjointClassesAxiom conclusion = conclusionMatch.getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getMembers() + MATCH_
				+ conclusionMatch.getMemberMatches();
	}

	@Override
	public String visit(IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return conclusionMatch.getParent() + MATCH_LEVEL_;
	}

	@Override
	public String visit(IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		IndexedSubClassOfAxiom conclusion = conclusionMatch.getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getSubClass() + MATCH_
				+ conclusionMatch.getSubClassMatch() + MATCH_SEP_
				+ conclusion.getSuperClass() + MATCH_
				+ conclusionMatch.getSuperClassMatch();
	}

	@Override
	public String visit(IndexedEquivalentClassesAxiomMatch1 conclusionMatch) {
		return conclusionMatch.getParent() + MATCH_LEVEL_;
	}

	@Override
	public String visit(IndexedEquivalentClassesAxiomMatch2 conclusionMatch) {
		IndexedEquivalentClassesAxiom conclusion = conclusionMatch.getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getFirstMember() + MATCH_
				+ conclusionMatch.getFirstMemberMatch() + MATCH_SEP_
				+ conclusion.getSecondMember() + MATCH_
				+ conclusionMatch.getSecondMemberMatch();
	}

	@Override
	public String visit(IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return conclusionMatch.getParent() + MATCH_LEVEL_;
	}

	@Override
	public String visit(IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		IndexedSubObjectPropertyOfAxiom conclusion = conclusionMatch.getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getSubPropertyChain() + MATCH_
				+ conclusionMatch.getSubPropertyChainMatch() + MATCH_SEP_
				+ conclusion.getSuperProperty() + MATCH_
				+ conclusionMatch.getSuperPropertyMatch();
	}

	@Override
	public String visit(IndexedObjectPropertyRangeAxiomMatch1 conclusionMatch) {
		return conclusionMatch.getParent() + MATCH_LEVEL_;
	}

	@Override
	public String visit(IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch) {
		IndexedObjectPropertyRangeAxiom conclusion = conclusionMatch.getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getProperty() + MATCH_
				+ conclusionMatch.getPropertyMatch() + MATCH_SEP_
				+ conclusion.getRange() + MATCH_
				+ conclusionMatch.getRangeMatch();
	}

	@Override
	public String visit(BackwardLinkMatch1 conclusionMatch) {
		BackwardLink conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getSource() + MATCH_
				+ conclusionMatch.getSourceMatch() + MATCH_SEP_
				+ conclusion.getRelation() + MATCH_
				+ conclusionMatch.getRelationMatch();
	}

	@Override
	public String visit(BackwardLinkMatch2 conclusionMatch) {
		BackwardLink conclusion = conclusionMatch.getParent().getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getDestination() + MATCH_
				+ conclusionMatch.getDestinationMatch();
	}

	@Override
	public String visit(SubClassInclusionComposedMatch1 conclusionMatch) {
		SubClassInclusionComposed conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getDestination() + MATCH_
				+ conclusionMatch.getDestinationMatch() + MATCH_SEP_
				+ conclusion.getSubsumer() + MATCH_
				+ conclusionMatch.getSubsumerMatch();
	}

	@Override
	public String visit(SubClassInclusionDecomposedMatch1 conclusionMatch) {
		SubClassInclusionDecomposed conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getDestination() + MATCH_
				+ conclusionMatch.getDestinationMatch();
	}

	@Override
	public String visit(SubClassInclusionDecomposedMatch2 conclusionMatch) {
		SubClassInclusionDecomposed conclusion = conclusionMatch.getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getSubsumer() + MATCH_
				+ conclusionMatch.getSubsumerMatch();
	}

	@Override
	public String visit(ForwardLinkMatch1 conclusionMatch) {
		ForwardLink conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getDestination() + MATCH_
				+ conclusionMatch.getDestinationMatch() + MATCH_SEP_
				+ conclusion.getChain() + MATCH_
				+ chainMatch(conclusionMatch.getFullChainMatch(),
						conclusionMatch.getChainStartPos());
	}

	@Override
	public String visit(ForwardLinkMatch2 conclusionMatch) {
		ForwardLink conclusion = conclusionMatch.getParent().getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ toString(conclusionMatch.getIntermediateRoots()) + MATCH_SEP_
				+ conclusion.getTarget() + MATCH_
				+ conclusionMatch.getTargetMatch();
	}

	@Override
	public String visit(PropagationMatch1 conclusionMatch) {
		Propagation conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getCarry() + MATCH_
				+ conclusionMatch.getCarryMatch();
	}

	@Override
	public String visit(PropagationMatch2 conclusionMatch) {
		Propagation conclusion = conclusionMatch.getParent().getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getRelation() + MATCH_
				+ conclusionMatch.getRelationMatch();
	}

	@Override
	public String visit(PropagationMatch3 conclusionMatch) {
		Propagation conclusion = conclusionMatch.getParent().getParent()
				.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getDestination() + MATCH_
				+ conclusionMatch.getDestinationMatch();
	}

	@Override
	public String visit(PropertyRangeMatch1 conclusionMatch) {
		PropertyRange conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getProperty() + MATCH_
				+ conclusionMatch.getPropertyMatch();
	}

	@Override
	public String visit(PropertyRangeMatch2 conclusionMatch) {
		PropertyRange conclusion = conclusionMatch.getParent().getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getRange() + MATCH_
				+ conclusionMatch.getRangeMatch();
	}

	@Override
	public String visit(SubPropertyChainMatch1 conclusionMatch) {
		SubPropertyChain conclusion = conclusionMatch.getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getSuperChain() + MATCH_
				+ chainMatch(conclusionMatch.getFullSuperChainMatch(),
						+conclusionMatch.getSuperChainStartPos());
	}

	@Override
	public String visit(SubPropertyChainMatch2 conclusionMatch) {
		SubPropertyChain conclusion = conclusionMatch.getParent().getParent();
		return conclusionMatch.getParent() + MATCH_LEVEL_//
				+ conclusion.getSubChain() + MATCH_
				+ chainMatch(conclusionMatch.getFullSubChainMatch(),
						+conclusionMatch.getSubChainStartPos());
	}

}
