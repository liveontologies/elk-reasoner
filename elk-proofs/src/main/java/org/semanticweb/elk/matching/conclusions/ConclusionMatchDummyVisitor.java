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

/**
 * A {@link ConclusionMatch.Visitor} that always returns {@code null}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output
 */
public class ConclusionMatchDummyVisitor<O>
		implements ConclusionMatch.Visitor<O> {

	protected O defaultVisit(ConclusionMatch conclusionMatch) {
		return null;
	}

	@Override
	public O visit(IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedDefinitionAxiomMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedDefinitionAxiomMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedObjectPropertyRangeAxiomMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(BackwardLinkMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(BackwardLinkMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(SubClassInclusionComposedMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(SubClassInclusionDecomposedMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(ForwardLinkMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(ForwardLinkMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(PropagationMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(PropagationMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(PropagationMatch3 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(PropertyRangeMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(PropertyRangeMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(SubPropertyChainMatch1 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

	@Override
	public O visit(SubPropertyChainMatch2 conclusionMatch) {
		return defaultVisit(conclusionMatch);
	}

}
