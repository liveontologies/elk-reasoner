/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.EnumMap;

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndexUpdater;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;

/**
 * Stores all data structures, e.g., the differential index, to
 * maintain the state of the reasoner in the incremental mode.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalReasonerState {

	final DifferentialIndex diffIndex;
	
	final EnumMap<IncrementalStages, Boolean> stageStatusMap = new EnumMap<IncrementalStages, Boolean>(IncrementalStages.class);
	
	IncrementalReasonerState(IndexedObjectCache objectCache, OntologyIndex ontIndex) {
		diffIndex = new DifferentialIndex(new DirectIndexUpdater(ontIndex), objectCache, ontIndex.getIndexedOwlNothing());
		
		initStageStatuses();
	}
	
	private void initStageStatuses() {
		for (IncrementalStages type : IncrementalStages.values()) {
			stageStatusMap.put(type, false);
		}
	}

	void setStageStatus(IncrementalStages stageType, boolean status) {
		stageStatusMap.put(stageType, status);
	}
	
	boolean getStageStatus(IncrementalStages stageType) {
		return stageStatusMap.get(stageType);
	}

	void resetAllStagesStatus() {
		for (IncrementalStages stage : stageStatusMap.keySet()) {
			stageStatusMap.put(stage, false);
		}		
	}	
}