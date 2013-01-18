/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

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
		
		if (stage.getClass().equals(IncrementalDeSaturationStage.class)) {
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
		
		if (stage.getClass().equals(IncrementalReSaturationStage.class)) {
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
				metrics_.updateIntegerMetric(CLEANED_CONTEXT_COUNT, cleanedCount);
				metrics_.updateIntegerMetric(RESATURATED_TO_THE_SAME_STATE, sameCount);
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
