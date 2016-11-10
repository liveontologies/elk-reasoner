package org.semanticweb.elk.matching.inferences;

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

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.matching.subsumers.IndexedObjectSomeValuesFromMatch;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedContextRootVisitor;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedRangeFiller;

abstract class LinkOfObjectSomeValuesFromMatch2<P>
		extends AbstractInferenceMatch<P> {

	private final IndexedObjectSomeValuesFromMatch premiseSuperExpressionMatch_;

	public LinkOfObjectSomeValuesFromMatch2(P parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		super(parent);
		this.premiseSuperExpressionMatch_ = premiseMatch
				.getSubsumerIndexedObjectSomeValuesFromMatch();
	}

	public IndexedObjectSomeValuesFromMatch getPremiseSuperExpressionMatch() {
		return premiseSuperExpressionMatch_;
	}

	ElkObjectProperty getPremisePropertyMatch(
			IndexedObjectProperty premiseProperty) {
		ElkObjectPropertyExpression premisePropertyMatch = premiseSuperExpressionMatch_
				.getPropertyMatch();
		if (premisePropertyMatch instanceof ElkObjectProperty) {
			return (ElkObjectProperty) premisePropertyMatch;
		} else {
			throw new ElkMatchException(premiseProperty, premisePropertyMatch);
		}
	}

	IndexedContextRootMatch getRootMatch(IndexedContextRoot root,
			final IndexedContextRootMatch.Factory factory) {
		return root.accept(
				new DummyIndexedContextRootVisitor<IndexedContextRootMatch>() {

					@Override
					protected IndexedContextRootMatch defaultVisit(
							IndexedClassExpression element) {
						return premiseSuperExpressionMatch_
								.getFillerRootMatch(factory);
					}

					@Override
					public IndexedContextRootMatch visit(
							IndexedRangeFiller element) {
						return premiseSuperExpressionMatch_
								.getRangeRootMatch(factory);
					}

				});
	}

}
