package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;

/**
 * A {@link ConclusionProducer} that combines two given
 * {@link ConclusionProducer}: all methods are executed first for the first
 * {@link ConclusionProducer} and then for the second.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class CombinedConclusionProducer implements ConclusionProducer {

	private final ConclusionProducer firstProducer_;

	private final ConclusionProducer secondProducer_;

	public CombinedConclusionProducer(ConclusionProducer firstProducer,
			ConclusionProducer secondProducer) {
		this.firstProducer_ = firstProducer;
		this.secondProducer_ = secondProducer;
	}

	@Override
	public void produce(Conclusion conclusion) {
		firstProducer_.produce(conclusion);
		secondProducer_.produce(conclusion);
	}

}
