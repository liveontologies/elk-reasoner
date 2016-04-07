/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class EnumerateContextsStage extends BasePostProcessingStage {

	private final AbstractReasonerState reasoner_;

	public EnumerateContextsStage(AbstractReasonerState reasoner) {
		reasoner_ = reasoner;
	}

	@Override
	public String getName() {
		return "Iterating over contexts";
	}

	@Override
	public void execute() throws ElkException {
		int cnt = 0;

		for (Context context : reasoner_.saturationState.getContexts()) {
			cnt += context.getComposedSubsumers().size();
		}

		cnt = 2 * cnt - cnt;
	}

}
