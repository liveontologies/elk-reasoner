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
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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