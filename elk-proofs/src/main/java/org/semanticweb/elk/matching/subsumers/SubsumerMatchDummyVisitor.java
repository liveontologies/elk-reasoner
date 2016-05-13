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

public class SubsumerMatchDummyVisitor<O> implements SubsumerMatch.Visitor<O> {

	protected O defaultVisit(IndexedClassEntityMatch match) {
		return defaultVisit((SubsumerElkObjectMatch) match);
	}

	protected O defaultVisit(IndexedObjectSomeValuesFromMatch match) {
		return defaultVisit((SubsumerElkObjectMatch) match);
	}

	protected O defaultVisit(IndexedObjectUnionOfMatch match) {
		return defaultVisit((SubsumerElkObjectMatch) match);
	}

	protected O defaultVisit(SubsumerElkObjectMatch match) {
		return defaultVisit((SubsumerMatch) match);
	}

	protected O defaultVisit(SubsumerMatch subsumerMatch) {
		// can be overridden in sub-classes
		return null;
	}

	@Override
	public O visit(IndexedClassMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedDataHasValueMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedIndividualMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectComplementOfMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectHasSelfMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectIntersectionOfMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectSomeValuesFromHasValueMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectSomeValuesFromSomeValuesFromMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectUnionOfOneOfMatch match) {
		return defaultVisit(match);
	}

	@Override
	public O visit(IndexedObjectUnionOfUnionOfMatch match) {
		return defaultVisit(match);
	}

}
