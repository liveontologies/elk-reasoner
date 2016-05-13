package org.semanticweb.elk.matching.conclusions;

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.SubsumerMatch;

/*
 * #%L
 * ELK Reasoner
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class SubClassInclusionComposedMatch1
		extends SubClassInclusionMatch<SubClassInclusionComposed> {

	private final IndexedContextRootMatch destinationMatch_;

	SubClassInclusionComposedMatch1(SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkClassExpression subsumerMatchValue) {
		super(parent, subsumerMatchValue);
		this.destinationMatch_ = destinationMatch;
	}

	SubClassInclusionComposedMatch1(SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkIndividual subsumerMatchValue) {
		super(parent, subsumerMatchValue);
		this.destinationMatch_ = destinationMatch;
	}
	
	SubClassInclusionComposedMatch1(SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			ElkObjectIntersectionOf fullSubsumerMatch,
			int subsumerPrefixLength) {
		super(parent, fullSubsumerMatch, subsumerPrefixLength);
		this.destinationMatch_ = destinationMatch;
	}

	SubClassInclusionComposedMatch1(SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch,
			SubsumerMatch subsumerMatch) {
		super(parent, subsumerMatch);
		this.destinationMatch_ = destinationMatch;
	}

	@Override
	IndexedClassExpression getSubsumer() {
		return getParent().getSubsumer();
	}

	public IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	@Override
	public <O> O accept(ClassConclusionMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
				SubClassInclusionComposed parent,
				IndexedContextRootMatch destinationMatch,
				ElkClassExpression subsumerMatchValue);
		
		SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
				SubClassInclusionComposed parent,
				IndexedContextRootMatch destinationMatch,
				ElkIndividual subsumerMatchValue);

		SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
				SubClassInclusionComposed parent,
				IndexedContextRootMatch destinationMatch,
				ElkObjectIntersectionOf fullSubsumerMatch,
				int subsumerPrefixLength);

		SubClassInclusionComposedMatch1 getSubClassInclusionComposedMatch1(
				SubClassInclusionComposed parent,
				IndexedContextRootMatch destinationMatch,
				SubsumerMatch subsumerMatch);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(SubClassInclusionComposedMatch1 conclusionMatch);

	}

}
