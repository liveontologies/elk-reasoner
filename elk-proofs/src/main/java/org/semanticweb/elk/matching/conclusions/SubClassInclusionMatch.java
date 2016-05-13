package org.semanticweb.elk.matching.conclusions;

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.matching.subsumers.IndexedClassEntityMatch;
import org.semanticweb.elk.matching.subsumers.IndexedClassMatch;
import org.semanticweb.elk.matching.subsumers.IndexedIndividualMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectComplementOfMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectHasSelfMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectIntersectionOfMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectUnionOfMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatchDummyVisitor;
import org.semanticweb.elk.matching.subsumers.SubsumerMatches;

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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

public abstract class SubClassInclusionMatch<P>
		extends AbstractClassConclusionMatch<P> {

	class FailingSubsumerMatcher<M extends SubsumerMatch>
			extends SubsumerMatchDummyVisitor<M> {
		@Override
		public M defaultVisit(SubsumerMatch subsumerMatch) {
			throw new ElkMatchException(getSubsumer(), subsumerMatch);
		}

	}

	private final SubsumerMatch subsumerMatch_;

	SubClassInclusionMatch(P parent, ElkClassExpression subsumerMatchValue) {
		super(parent);
		this.subsumerMatch_ = SubsumerMatches.create(subsumerMatchValue);
	}
	
	SubClassInclusionMatch(P parent, ElkIndividual subsumerMatchValue) {
		super(parent);
		this.subsumerMatch_ = SubsumerMatches.create(subsumerMatchValue);
	}

	SubClassInclusionMatch(P parent,
			ElkObjectIntersectionOf subsumerMatchFullValue,
			int subsumerMatchPrefixLength) {
		super(parent);
		if (subsumerMatchPrefixLength == 1) {
			this.subsumerMatch_ = SubsumerMatches.create(
					subsumerMatchFullValue.getClassExpressions().get(0));
		} else {
			this.subsumerMatch_ = SubsumerMatches.create(subsumerMatchFullValue,
					subsumerMatchPrefixLength);
		}
	}

	SubClassInclusionMatch(P parent, SubsumerMatch subsumerMatch) {
		super(parent);
		this.subsumerMatch_ = subsumerMatch;
	}

	abstract IndexedClassExpression getSubsumer();

	public IndexedClassEntityMatch getSubsumerIndexedClassEntityMatch() {
		return subsumerMatch_
				.accept(new FailingSubsumerMatcher<IndexedClassEntityMatch>() {

					@Override
					public IndexedClassEntityMatch defaultVisit(
							IndexedClassEntityMatch match) {
						return match;
					}

				});
	}

	public IndexedClassMatch getSubsumerIndexedClassMatch() {
		return subsumerMatch_
				.accept(new FailingSubsumerMatcher<IndexedClassMatch>() {

					@Override
					public IndexedClassMatch visit(IndexedClassMatch match) {
						return match;
					}

				});
	}

	public IndexedIndividualMatch getSubsumerIndexedIndividualMatch() {
		return subsumerMatch_
				.accept(new FailingSubsumerMatcher<IndexedIndividualMatch>() {

					@Override
					public IndexedIndividualMatch visit(
							IndexedIndividualMatch match) {
						return match;
					}

				});
	}

	public IndexedObjectComplementOfMatch getSubsumerIndexedObjectComplementOfMatch() {
		return subsumerMatch_.accept(
				new FailingSubsumerMatcher<IndexedObjectComplementOfMatch>() {

					@Override
					public IndexedObjectComplementOfMatch visit(
							IndexedObjectComplementOfMatch match) {
						return match;
					}

				});
	}

	public IndexedObjectHasSelfMatch getSubsumerIndexedObjectHasSelfMatch() {
		return subsumerMatch_.accept(
				new FailingSubsumerMatcher<IndexedObjectHasSelfMatch>() {

					@Override
					public IndexedObjectHasSelfMatch visit(
							IndexedObjectHasSelfMatch match) {
						return match;
					}

				});
	}

	public IndexedObjectIntersectionOfMatch getSubsumerIndexedObjectIntersectionOfMatch() {
		return subsumerMatch_.accept(
				new FailingSubsumerMatcher<IndexedObjectIntersectionOfMatch>() {

					@Override
					public IndexedObjectIntersectionOfMatch visit(
							IndexedObjectIntersectionOfMatch match) {
						return match;
					}

				});
	}

	public IndexedObjectSomeValuesFromMatch getSubsumerIndexedObjectSomeValuesFromMatch() {
		return subsumerMatch_.accept(
				new FailingSubsumerMatcher<IndexedObjectSomeValuesFromMatch>() {

					@Override
					public IndexedObjectSomeValuesFromMatch defaultVisit(
							IndexedObjectSomeValuesFromMatch match) {
						return match;
					}

				});
	}

	public IndexedObjectUnionOfMatch getSubsumerIndexedObjectUnionOfMatch() {
		return subsumerMatch_.accept(
				new FailingSubsumerMatcher<IndexedObjectUnionOfMatch>() {

					@Override
					public IndexedObjectUnionOfMatch defaultVisit(
							IndexedObjectUnionOfMatch match) {
						return match;
					}
				});
	}

	public SubsumerMatch getSubsumerMatch() {
		return subsumerMatch_;
	}

}
