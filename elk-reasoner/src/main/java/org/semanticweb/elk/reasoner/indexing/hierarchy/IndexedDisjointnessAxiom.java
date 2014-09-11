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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleToIndexWriter;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the disjointness inference rule for indexed class expressions
 * 
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedDisjointnessAxiom extends IndexedAxiom {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedDisjointnessAxiom.class);

	/**
	 * {@link IndexedClassExpression}s that have at least two equal occurrences
	 * (according to the {@link Object#equals(Object)} method) in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	private final Set<IndexedClassExpression> inconsistentMembers_;
	/**
	 * {@link IndexedClassExpression}s that occur exactly once in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	private final Set<IndexedClassExpression> disjointMembers_;

	/**
	 * This counts how often this {@link IndexedDisjointnessAxiom} occurs in
	 * the ontology.
	 */
	int occurrenceNo = 0;

	IndexedDisjointnessAxiom(List<IndexedClassExpression> members) {
		this.inconsistentMembers_ = new ArrayHashSet<IndexedClassExpression>(1);
		this.disjointMembers_ = new ArrayHashSet<IndexedClassExpression>(2);
		for (IndexedClassExpression member : members) {
			if (inconsistentMembers_.contains(member))
				continue;
			if (!disjointMembers_.add(member)) {
				disjointMembers_.remove(member);
				inconsistentMembers_.add(member);
			}
		}
	}

	/**
	 * @return {@link IndexedClassExpression}s that have at least two equal
	 *         occurrences (according to the {@link Object#equals(Object)}
	 *         method) in this {@link IndexedDisjointnessAxiom}
	 */
	public Set<IndexedClassExpression> getInconsistentMembers() {
		return inconsistentMembers_;
	}

	/**
	 * {@link IndexedClassExpression}s that occur exactly once in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	public Set<IndexedClassExpression> getDisjointMembers() {
		return disjointMembers_;
	}

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	protected void updateOccurrenceNumbers(final ModifiableOntologyIndex index,
			final RuleToIndexWriter ruleWriter, final int increment) {

		if (occurrenceNo == 0 && increment > 0) {
			// first occurrence of this axiom
			registerCompositionRule(index);
		}

		occurrenceNo += increment;

		if (occurrenceNo == 0 && increment < 0) {
			// last occurrence of this axiom
			deregisterCompositionRule(index);
		}
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toStringStructural() {
		List<IndexedClassExpression> members = new LinkedList<IndexedClassExpression>();
		for (IndexedClassExpression inconsistentMember : inconsistentMembers_) {
			// each inconsistent member is added two times
			members.add(inconsistentMember);
			members.add(inconsistentMember);
		}
		members.addAll(disjointMembers_);
		return "DisjointClasses(" + members + ")";
	}

	private void registerCompositionRule(ModifiableOntologyIndex index) {
		// to derive contradiction from inconsistent disjointness axioms
		ContradictionFromDisjointnessRule.addRulesFor(this, index);
		// to derive disjoint subsumers from subsumers 
		DisjointSubsumerFromMemberRule.addRulesFor(this, index);
	}

	private void deregisterCompositionRule(ModifiableOntologyIndex index) {
		ContradictionFromDisjointnessRule.removeRulesFor(this, index);
		DisjointSubsumerFromMemberRule.removeRulesFor(this, index);
	}

}
