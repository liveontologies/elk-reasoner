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
import org.semanticweb.elk.matching.subsumers.IndexedObjectUnionOfMatchVisitor;
import org.semanticweb.elk.matching.subsumers.SubsumerElkObjectMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatchDummyVisitor;
import org.semanticweb.elk.matching.subsumers.SubsumerMatches;
import org.semanticweb.elk.owl.interfaces.ElkClass;

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
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.DummyElkObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

public abstract class SubClassInclusionMatch<P>
		extends AbstractClassConclusionMatch<P> {

	class FailingSubsumerMatcher<M extends SubsumerMatch>
			extends SubsumerMatchDummyVisitor<M> {
		@Override
		public M defaultVisit(SubsumerMatch subsumerMatch) {
			return failSubsumerMatch();
		}

	}

	class FailingElkObjectMatcher<O> extends DummyElkObjectVisitor<O> {
		@Override
		public O defaultVisit(ElkObject object) {
			return failSubsumerMatch();
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

	private <O> O failSubsumerMatch() {
		throw new ElkMatchException(getSubsumer(), subsumerMatch_);
	}

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

	public <O> O accept(final IndexedObjectUnionOfMatchVisitor<O> visitor) {

		if (subsumerMatch_ instanceof SubsumerElkObjectMatch) {
			return ((SubsumerElkObjectMatch) subsumerMatch_).getValue()
					.accept(new FailingElkObjectMatcher<O>() {

						@Override
						public O visit(ElkObjectUnionOf object) {
							return visitor.visit(object);
						}

						@Override
						public O visit(ElkObjectOneOf object) {
							return visitor.visit(object);
						}

					});

		}
		// else
		return failSubsumerMatch();

	}

	public ElkClass getSubsumerElkClassMatch() {
		if (subsumerMatch_ instanceof IndexedClassMatch) {
			return ((IndexedClassMatch) subsumerMatch_).getValue();
		}
		// else
		return failSubsumerMatch();
	}

	public ElkIndividual getSubsumerElkIndividualMatch() {
		if (subsumerMatch_ instanceof IndexedIndividualMatch) {
			return ((IndexedIndividualMatch) subsumerMatch_).getValue();
		}
		// else
		return failSubsumerMatch();
	}

	public ElkObjectComplementOf getSubsumerElkObjectComplementOfMatch() {
		if (subsumerMatch_ instanceof IndexedObjectComplementOfMatch) {
			return ((IndexedObjectComplementOfMatch) subsumerMatch_).getValue();
		}
		// else
		return failSubsumerMatch();
	}

	public ElkObjectHasSelf getSubsumerIndexedObjectHasSelfMatch() {
		if (subsumerMatch_ instanceof IndexedObjectHasSelfMatch) {
			return ((IndexedObjectHasSelfMatch) subsumerMatch_).getValue();
		}
		// else
		return failSubsumerMatch();
	}

	public IndexedObjectIntersectionOfMatch getSubsumerIndexedObjectIntersectionOfMatch() {
		if (subsumerMatch_ instanceof IndexedObjectIntersectionOfMatch) {
			return ((IndexedObjectIntersectionOfMatch) subsumerMatch_);
		}
		// else
		return failSubsumerMatch();
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
