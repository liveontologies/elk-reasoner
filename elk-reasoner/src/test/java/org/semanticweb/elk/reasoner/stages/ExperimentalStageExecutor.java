/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazyCollectionMinusSet;

/**
 * Executor for various experimental code, checks before and after various stages, etc.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ExperimentalStageExecutor extends LoggingStageExecutor {

	private Map<IndexedClassExpression, Set<IndexedClassExpression>> contexts_ = null;
	
	
	@Override
	public void execute(ReasonerStage stage) throws ElkException {
		
		AbstractReasonerStage reasonerStage = (AbstractReasonerStage) stage;
		
		if (stage.getClass().equals(IncrementalDeSaturationStage.class)) {
			contexts_ = new ArrayHashMap<IndexedClassExpression, Set<IndexedClassExpression>>(1024);
			//toClean_ = ((AbstractReasonerStage)stage).reasoner.getContextMap();
			for (IndexedClassExpression ice : reasonerStage.reasoner.ontologyIndex.getIndexedClassExpressions()) {
				Context context = ice.getContext();
				
				if (context == null)
					continue;
				
				contexts_.put(ice, new HashSet<IndexedClassExpression>(context.getSubsumers()));
			}
			
		}
		
		super.execute(stage);
		
		if (stage.getClass().equals(IncrementalReSaturationStage.class)) {
			//Map<IndexedClassExpression, Context> resaturatedContexts = ((AbstractReasonerStage)stage).reasoner.getContextMap();
			//how many are exactly the same (wrt atomic subsumers)?
			int sameCount = 0;
			int cleanedCount = reasonerStage.reasoner.saturationState.getNotSaturatedContexts().size();
			
			for (IndexedClassExpression ice : reasonerStage.reasoner.saturationState.getNotSaturatedContexts()) {
				if (ice.getContext() == null || !contexts_.containsKey(ice)) {
					continue;
				}
				
				if (sameSubsumers(contexts_.get(ice), ice.getContext())) {
					sameCount++;
				}
				else {
					//System.err.println("context " + ice + " changed: was " + contexts_.get(ice) + ", now: " + ice.getContext().getSubsumers());
				}
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
