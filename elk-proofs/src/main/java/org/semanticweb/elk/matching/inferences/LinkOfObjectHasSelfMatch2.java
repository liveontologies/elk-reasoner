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
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;

abstract class LinkOfObjectHasSelfMatch2<P> extends AbstractInferenceMatch<P> {

	private final ElkObjectHasSelf premiseSubsumerMatch_;

	public LinkOfObjectHasSelfMatch2(P parent,
			SubClassInclusionDecomposedMatch2 premiseMatch) {
		super(parent);
		this.premiseSubsumerMatch_ = premiseMatch
				.getSubsumerIndexedObjectHasSelfMatch().getValue();
	}

	ElkObjectHasSelf getPremiseSuperExpressionMatch() {
		return premiseSubsumerMatch_;
	}

	ElkObjectProperty getPremisePropertyMatch(
			IndexedObjectProperty premiseProperty) {
		ElkObjectPropertyExpression premisePropertyMatch = premiseSubsumerMatch_
				.getProperty();
		if (premisePropertyMatch instanceof ElkObjectProperty) {
			return (ElkObjectProperty) premisePropertyMatch;
		} else {
			throw new ElkMatchException(premiseProperty, premisePropertyMatch);
		}
	}

}
