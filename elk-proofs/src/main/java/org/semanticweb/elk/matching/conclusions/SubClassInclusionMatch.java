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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

public abstract class SubClassInclusionMatch<P>
		extends AbstractClassConclusionMatch<P> {

	private final SubsumerMatch subsumerMatch_;

	SubClassInclusionMatch(P parent, SubsumerMatch subsumerMatch) {
		super(parent);
		this.subsumerMatch_ = subsumerMatch;
	}

	SubClassInclusionMatch(P parent, ElkClassExpression subsumerGeneralMatch) {
		super(parent);
		this.subsumerMatch_ = new SubsumerGeneralMatch(subsumerGeneralMatch);
	}

	SubClassInclusionMatch(P parent,
			ElkObjectIntersectionOf subsumerFullConjunctionMatch,
			int subsumerConjunctionPrefixLength) {
		super(parent);
		if (subsumerConjunctionPrefixLength == 1) {
			this.subsumerMatch_ = new SubsumerGeneralMatch(
					subsumerFullConjunctionMatch.getClassExpressions().get(0));
		} else {
			this.subsumerMatch_ = new SubsumerPartialConjunctionMatch(
					subsumerFullConjunctionMatch,
					subsumerConjunctionPrefixLength);
		}
	}

	abstract IndexedClassExpression getSubsumer();

	SubsumerMatch getSubsumerMatch() {
		return subsumerMatch_;
	}

	public ElkClassExpression getSubsumerGeneralMatch() {
		return subsumerMatch_.getGeneralMatch(getSubsumer());
	}

	public ElkObjectIntersectionOf getSubsumerFullConjunctionMatch() {
		return subsumerMatch_.getFullConjunctionMatch(getSubsumer());
	}

	public int getSubsumerConjunctionPrefixLength() {
		return subsumerMatch_.getConjunctionPrefixLength(getSubsumer());
	}

}
