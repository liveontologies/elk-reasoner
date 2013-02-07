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

public class ReasonerStageManager {

	final AbstractReasonerState reasoner;

	final ReasonerStage changesLoadingStage;
	final ReasonerStage classSaturationStage;
	final ReasonerStage classTaxonomyComputationStage;
	final ReasonerStage consistencyCheckingStage;
	final ReasonerStage contextInitializationStage;
	final ReasonerStage incrementalAdditionStage;
	final ReasonerStage incrementalAdditionInitializationStage;
	final ReasonerStage incrementalCompletionStage;
	final ReasonerStage incrementalClassTaxonomyComputationStage;
	final ReasonerStage incrementalConsistencyCheckingStage;
	final ReasonerStage incrementalContextCleaningStage;
	final ReasonerStage incrementalDeletionStage;
	final ReasonerStage incrementalDeletionInitializationStage;
	final ReasonerStage incrementalTaxonomyCleaningStage;
	final ReasonerStage initializeContextsAfterDeletionsStage;
	final ReasonerStage initializeContextsAfterCleaningStage;
	final ReasonerStage instanceTaxonomyComputationStage;
	final ReasonerStage ontologyLoadingStage;
	final ReasonerStage propertyHierarchyCompositionComputationStage;
	final ReasonerStage propertyInitializationStage;
	final ReasonerStage propertyReflexivityComputationStage;

	ReasonerStageManager(AbstractReasonerState reasoner) {
		this.reasoner = reasoner;

		this.changesLoadingStage = new ChangesLoadingStage(this);
		this.classSaturationStage = new ClassSaturationStage(this);
		this.classTaxonomyComputationStage = new ClassTaxonomyComputationStage(this);
		this.consistencyCheckingStage = new ConsistencyCheckingStage(this);
		this.contextInitializationStage = new ContextInitializationStage(this);
		this.incrementalAdditionStage = new IncrementalAdditionStage(this);
		this.incrementalAdditionInitializationStage = new IncrementalAdditionInitializationStage(this);
		this.incrementalCompletionStage = new IncrementalCompletionStage(this);
		this.incrementalClassTaxonomyComputationStage = new IncrementalClassTaxonomyComputationStage(this);
		this.incrementalConsistencyCheckingStage = new IncrementalConsistencyCheckingStage(this);
		this.incrementalContextCleaningStage = new IncrementalContextCleaningStage(this);
		this.incrementalDeletionStage = new IncrementalDeletionStage(this);
		this.incrementalDeletionInitializationStage = new IncrementalDeletionInitializationStage(this);
		this.incrementalTaxonomyCleaningStage = new IncrementalTaxonomyCleaningStage(this);
		this.initializeContextsAfterDeletionsStage = new InitializeContextsAfterDeletionsStage(this);
		this.initializeContextsAfterCleaningStage = new InitializeContextsAfterCleaningStage(this);
		this.instanceTaxonomyComputationStage = new InstanceTaxonomyComputationStage(this);
		this.ontologyLoadingStage = new OntologyLoadingStage(this);
		this.propertyHierarchyCompositionComputationStage = new PropertyHierarchyCompositionComputationStage(this);
		this.propertyInitializationStage = new PropertyInitializationStage(this);
		this.propertyReflexivityComputationStage = new PropertyReflexivityComputationStage(this);
	}
}
