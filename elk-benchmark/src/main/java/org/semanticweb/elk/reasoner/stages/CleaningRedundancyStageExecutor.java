/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Benchmarking Package
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazyCollectionMinusSet;

/**
 * Measures how many de-saturated (and thus cleaned) contexts get re-saturated
 * to the same state (w.r.t. the set of atomic subsumers)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CleaningRedundancyStageExecutor extends SimpleStageExecutor {

	private Map<IndexedClassExpression, Set<IndexedClassExpression>> subsumerMap_ = null;
	private final Metrics metrics_;
	
	public static final String CLEANED_CONTEXT_COUNT = "contexts.cleaned";
	public static final String RESATURATED_TO_THE_SAME_STATE = "contexts.resaturated-to-the-same-state";
	
	public CleaningRedundancyStageExecutor(Metrics m) {
		metrics_ = m;
	}
	
	@Override
	public void execute(ReasonerStage stage) throws ElkException {
		
		AbstractReasonerStage reasonerStage = (AbstractReasonerStage) stage;
		
		if (stage.getClass().equals(IncrementalDeletionStage.class)) {
			subsumerMap_ = new ArrayHashMap<IndexedClassExpression, Set<IndexedClassExpression>>(1024);
			//toClean_ = ((AbstractReasonerStage)stage).reasoner.getContextMap();
			for (IndexedClassExpression ice : reasonerStage.reasoner.ontologyIndex.getIndexedClassExpressions()) {
				Context context = ice.getContext();
				
				if (context == null)
					continue;
				
				subsumerMap_.put(ice, new HashSet<IndexedClassExpression>(context.getSubsumers()));
			}
			
		}
		
		super.execute(stage);
		
		if (stage.getClass().equals(IncrementalAdditionStage.class)) {
			//Map<IndexedClassExpression, Context> resaturatedContexts = ((AbstractReasonerStage)stage).reasoner.getContextMap();
			//how many are exactly the same (wrt atomic subsumers)?
			int sameCount = 0;
			int cleanedCount = reasonerStage.reasoner.saturationState.getNotSaturatedContexts().size();
			
			for (IndexedClassExpression ice : reasonerStage.reasoner.saturationState.getNotSaturatedContexts()) {
				if (ice.getContext() == null || !subsumerMap_.containsKey(ice)) {
					continue;
				}
				
				if (sameSubsumers(subsumerMap_.get(ice), ice.getContext())) {
					sameCount++;
				}
			}
			
			if (cleanedCount > 0) {
				metrics_.updateLongMetric(CLEANED_CONTEXT_COUNT, cleanedCount);
				metrics_.updateLongMetric(RESATURATED_TO_THE_SAME_STATE, sameCount);
			}
			
			System.err.println(cleanedCount + " contexts cleaned, " + sameCount + " are the same after re-saturation");
		}
	}


	private boolean sameSubsumers(Set<IndexedClassExpression> subsumers, Context context) {
		for (IndexedClassExpression ice : new LazyCollectionMinusSet<IndexedClassExpression>(subsumers, context.getSubsumers())) {
			if (ice instanceof IndexedClass) {
				return false;
			}
		}
		
		for (IndexedClassExpression ice : new LazyCollectionMinusSet<IndexedClassExpression>(context.getSubsumers(), subsumers)) {
			
			if (ice instanceof IndexedClass) {
				return false;
			}
		}

		return true;
	}

}
