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
 *         pavel.klinov@uni-ulm.de
 */
public class PostProcessingStageExecutor extends SimpleStageExecutor {

	public static final Multimap<Class<?>, Class<?>> STATIC_MAP = new HashListMultimap<Class<?>, Class<?>>();

	public static final Class<?> CLASS_TAXONOMY_COMPUTATION = ClassTaxonomyComputationStage.class;
	
	/*
	 * STATIC INT
	 */
	static {
		// init post processing map
		/*postProcesingMap.add(
				PropertyHierarchyCompositionComputationStage.class,
				SaturatedPropertyChainCheckingStage.class);
		postProcesingMap.add(IncrementalDeletionStage.class,
				ContextSaturationFlagCheckingStage.class);
		postProcesingMap.add(IncrementalDeletionStage.class,
				SaturationGraphValidationStage.class);
		postProcesingMap.add(IncrementalAdditionStage.class,
				CheckContextInvariants.class);
		postProcesingMap.add(IncrementalTaxonomyCleaningStage.class,
				ValidateTaxonomyStage.class);*/
		
		STATIC_MAP.add(IncrementalDeletionInitializationStage.class,
				EnumerateContextsStage.class);		
	}
	
	private final Multimap<Class<?>, Class<?>> postProcessingMap_;

	public PostProcessingStageExecutor() {
		postProcessingMap_ = STATIC_MAP;;
	}
	
	public PostProcessingStageExecutor(Class<?>... stageMap) {
		postProcessingMap_ = new HashListMultimap<Class<?>, Class<?>>();
		
		for (int i = 0; i < stageMap.length - 1; i += 2) {
			postProcessingMap_.add(stageMap[i], stageMap[i + 1]);
		}
	}

	@Override
	public void execute(ReasonerStage stage) throws ElkException {
		super.execute(stage);
		
		// FIXME: get rid of casts
		try {
			for (PostProcessingStage ppStage : instantiate(
					postProcessingMap_.get(stage.getClass()),
					((AbstractReasonerStage) stage).reasoner)) {
				
				//ElkTimer.getNamedTimer(ppStage.getName()).start();
				
				ppStage.execute();
				
				//ElkTimer.getNamedTimer(ppStage.getName()).stop();
			}
		} catch (Exception e) {
			throw new ElkRuntimeException(e);
		}
	}


	private Collection<PostProcessingStage> instantiate(
			Collection<Class<?>> collection, AbstractReasonerState reasoner)
			throws Exception {
		List<PostProcessingStage> stages = new ArrayList<PostProcessingStage>(1);

		for (Class<?> stageClass : collection) {
			Constructor<?> constructor = stageClass
					.getConstructor(AbstractReasonerState.class);
			PostProcessingStage stage = (PostProcessingStage) constructor
					.newInstance(reasoner);

			stages.add(stage);
		}

		return stages;
	}

}
