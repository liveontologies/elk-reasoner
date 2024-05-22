package org.semanticweb.elk.reasoner.proof;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2024 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * The union of multiple proofs. Inferences from this proof deriving some
 * conclusion are union of inferences from the supplied proofs deriving that
 * conclusion.
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 *
 * @param <I>
 *            the type of inferences provided by this proof
 */
class ReasonerProofUnion<I extends ReasonerInference<?>>
		implements ReasonerProof<I> {

	private final Iterable<? extends ReasonerProof<? extends I>> proofs_;

	public ReasonerProofUnion(
			final Iterable<? extends ReasonerProof<? extends I>> proofs) {
		Preconditions.checkNotNull(proofs);
		this.proofs_ = proofs;
	}

	@Override
	public Collection<? extends I> getInferences(final Object conclusion) {

		final List<I> result = new ArrayList<I>();

		for (final ReasonerProof<? extends I> proof : proofs_) {
			final Collection<? extends I> infs = proof
					.getInferences(conclusion);
			if (infs != null) {
				result.addAll(infs);
			}
		}

		return result;
	}

}
