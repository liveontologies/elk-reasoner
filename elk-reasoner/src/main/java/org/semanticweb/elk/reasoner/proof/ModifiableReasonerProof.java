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

/**
 * A {@link ReasonerProof} whose inferences may change
 * 
 * @author Yevgeny Kazakov
 *
 * @param <I>
 *            the type of inferences used in this proof
 */
public interface ModifiableReasonerProof<I extends ReasonerInference<?>>
		extends ReasonerProof<I>, ReasonerProducer<I> {

	/**
	 * Add the given inference to this {@link ReasonerProof}
	 * 
	 * @param inference
	 *            the {@link ReasonerInference} to be added
	 */
	@Override
	void produce(I inference);

	/**
	 * Remove all inferences from this {@link ReasonerProof}
	 */
	void clear();

}
