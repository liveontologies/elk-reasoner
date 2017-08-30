/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.tracing;

import java.util.Collection;
import java.util.Set;

/**
 * A {@link ModifiableTracingProof} in which the access methods are
 * synchronized.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <I>
 *            the type of inferences stored in this
 *            {@link ModifiableTracingProof}
 */
public class SynchronizedModifiableTracingProof<I extends TracingInference>
		extends ModifiableTracingProofImpl<I> {

	@Override
	public synchronized Collection<? extends I> getInferences(
			Conclusion conclusion) {
		return super.getInferences(conclusion);
	}

	@Override
	public synchronized void produce(I inference) {
		super.produce(inference);
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized Set<? extends Conclusion> getAllConclusions() {
		return super.getAllConclusions();
	}

}
