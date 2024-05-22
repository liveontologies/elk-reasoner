package org.semanticweb.elk.reasoner.proof;

import java.util.Arrays;

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

import java.util.Collection;
import java.util.Collections;

/**
 * An object from which one can retrieve inferences deriving conclusions.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <I>
 *            the type of inferences maintained by this proof
 */
public interface ReasonerProof<I extends ReasonerInference<?>> {

	/**
	 * @param conclusion
	 *            the conclusion for which to retrieve the inferences
	 * @return the inferences from this proof that derive the given conclusion
	 */
	Collection<? extends I> getInferences(Object conclusion);

	static class Empty<I extends ReasonerInference<?>>
			implements ReasonerProof<I> {

		@Override
		public Collection<? extends I> getInferences(Object conclusion) {
			return Collections.emptyList();
		}

	}

	static ReasonerProof<ReasonerInference<?>> EMPTY_ = new Empty<>();

	/**
	 * @return a {@link ReasonerProof} the empty set of inferences for every
	 *         conclusion.
	 * 
	 * @param <I>
	 *            the type of inferences used in this proof
	 */
	@SuppressWarnings("unchecked")
	public static <I extends ReasonerInference<?>> ReasonerProof<I> empty() {
		return (ReasonerProof<I>) Empty.EMPTY_;
	}

	/**
	 * @param <I>
	 *            the type of inferences provided by this proof
	 * @param proofs
	 *            the {@link ReasonerProof}s to be combined
	 * 
	 * @return the proof consisting of all inferences of the given proofs.
	 */
	public static <I extends ReasonerInference<?>> ReasonerProof<I> union(
			final Iterable<? extends ReasonerProof<? extends I>> proofs) {
		return new ReasonerProofUnion<I>(proofs);
	}

	/**
	 * @param <I>
	 *            the type of inferences provided by this proof
	 * @param proofs
	 *            the {@link ReasonerProof}s to be combined
	 * 
	 * @return the proof consisting of all inferences of the given proofs.
	 */
	@SafeVarargs
	public static <I extends ReasonerInference<?>> ReasonerProof<I> union(
			ReasonerProof<? extends I>... proofs) {
		return new ReasonerProofUnion<I>(Arrays.asList(proofs));
	}

}
