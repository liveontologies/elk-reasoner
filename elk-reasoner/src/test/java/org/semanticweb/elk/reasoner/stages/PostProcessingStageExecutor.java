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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PostProcessingStageExecutor extends LoggingStageExecutor {

	static final Multimap<Class<?>, Class<?>> postProcesingMap = new HashListMultimap<Class<?>, Class<?>>();
	
	/*
	 * STATIC INT
	 */
	static {
		//init post processing map
		postProcesingMap.add(PropertyHierarchyCompositionComputationStage.class, SaturatedPropertyChainCheckingStage.class);
		postProcesingMap.add(IncrementalDeSaturationStage.class, ContextSaturationFlagCheckingStage.class);
		postProcesingMap.add(IncrementalContextCleaningStage.class, CheckCleaningStage.class);
		postProcesingMap.add(IncrementalAdditionInitializationStage.class, SaturationGraphValidationStage.class);
		postProcesingMap.add(IncrementalReSaturationStage.class, RandomContextResaturationStage.class);
		postProcesingMap.add(IncrementalClassTaxonomyComputationStage.class, ValidateTaxonomyStage.class);
	}
	
	@Override
	public void complete(ReasonerStage stage) throws ElkException {
		super.complete(stage);
		
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Starting post processing...");
		}
		
		try {
			for (ReasonerStage ppStage : instantiate(postProcesingMap.get(stage.getClass()), ((AbstractReasonerStage)stage).reasoner)) {
				super.complete(ppStage);
			}
		} catch (Exception e) {
			throw new ElkRuntimeException(e);
		}
		
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Post processing finished");
		}

	}

	private Collection<ReasonerStage> instantiate(Collection<Class<?>> collection, AbstractReasonerState reasoner) throws Exception {
		List<ReasonerStage> stages = new ArrayList<ReasonerStage>(1);
		
		for (Class<?> stageClass : collection) {
			Constructor<?> constructor = stageClass.getConstructor(AbstractReasonerState.class);
			ReasonerStage stage = (ReasonerStage) constructor.newInstance(reasoner);
			
			stages.add(stage);
		}
		
		return stages;
	}
	
}
