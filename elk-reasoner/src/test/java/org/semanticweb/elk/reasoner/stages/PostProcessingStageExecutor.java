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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PostProcessingStageExecutor extends LoggingStageExecutor {

	static final Multimap<String, ReasonerStage> postProcesingMap = new HashListMultimap<String, ReasonerStage>();
	
	/*
	 * STATIC INT
	 */
	static {
		//TODO init post processing map
	}
	
	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		super.complete(stage);
		
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Starting post processing...");
		}
		
		for (ReasonerStage ppStage : postProcesingMap.get(stage.getName())) {
			super.complete(ppStage);
		}
		
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Post processing finished");
		}

	}

	
}
