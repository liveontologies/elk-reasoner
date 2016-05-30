package org.semanticweb.elk.matching.subsumers;

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

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.visitors.DummyElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * A helper class for converting {@link ElkObject}s to the corresponding
 * {@link SubsumerMatch}.
 * 
 * @author Yevgeny Kazakov
 *
 */
class SubsumerMatcherVisitor extends DummyElkObjectVisitor<SubsumerMatch> {

	private final static ElkObjectVisitor<SubsumerMatch> INSTANCE_ = new SubsumerMatcherVisitor();

	public static ElkObjectVisitor<SubsumerMatch> getInstance() {
		return INSTANCE_;
	}

	public SubsumerMatch defaultVisit(ElkIndividual match) {
		return new IndexedIndividualMatch((ElkIndividual) match);
	}

	@Override
	public SubsumerMatch visit(ElkAnonymousIndividual match) {
		return defaultVisit(match);
	}

	@Override
	public SubsumerMatch visit(ElkClass match) {
		return new IndexedClassMatch(match);
	}

	@Override
	public SubsumerMatch visit(ElkDataHasValue match) {
		return new IndexedDataHasValueMatch(match);
	}

	@Override
	public SubsumerMatch visit(ElkNamedIndividual match) {
		return defaultVisit(match);
	}

	@Override
	public SubsumerMatch visit(ElkObjectComplementOf match) {
		return new IndexedObjectComplementOfMatch(match);
	}

	@Override
	public SubsumerMatch visit(ElkObjectHasSelf match) {
		return new IndexedObjectHasSelfMatch(match);
	}

	@Override
	public SubsumerMatch visit(ElkObjectHasValue match) {
		return new SubsumerObjectHasValueMatch(match);
	}

	@Override
	public SubsumerMatch visit(ElkObjectIntersectionOf match) {
		switch (match.getClassExpressions().size()) {
		case 0:
			return new SubsumerEmptyObjectIntersectionOfMatch(match);
		case 1:
			return new SubsumerSingletonObjectIntersectionOfMatch(match);
		default:
			return new IndexedObjectIntersectionOfMatch(match);
		}
	}

	@Override
	public SubsumerMatch visit(ElkObjectOneOf match) {
		switch (match.getIndividuals().size()) {
		case 0:
			return new SubsumerEmptyObjectOneOfMatch(match);
		case 1:
			return new SubsumerSingletonObjectOneOfMatch(match);
		default:
			return new SubsumerObjectOneOfMatch(match);
		}
	}

	@Override
	public SubsumerMatch visit(ElkObjectSomeValuesFrom match) {
		return new SubsumerObjectSomeValuesFromMatch(match);
	}

	@Override
	public SubsumerMatch visit(ElkObjectUnionOf match) {
		switch (match.getClassExpressions().size()) {
		case 0:
			return new SubsumerEmptyObjectUnionOfMatch(match);
		case 1:
			return new SubsumerSingletonObjectUnionOfMatch(match);
		default:
			return new SubsumerObjectUnionOfMatch(match);
		}
	}

}