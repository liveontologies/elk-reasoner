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

	final AbstractReasonerStage ontologyLoadingStage, changesLoadingStage,
			propertyInitializationStage, propertyReflexivityComputationStage,
			propertyHierarchyCompositionComputationStage,
			contextInitializationStage, consistencyCheckingStage,
			classSaturationStage, classTaxonomyComputationStage,
			incrementalCompletionStage, incrementalDeletionInitializationStage,
			incrementalDeletionStage, initializeContextsAfterDeletionsStage,
			incrementalContextCleaningStage,
			initializeContextsAfterCleaningStage,
			incrementalAdditionInitializationStage, incrementalAdditionStage,
			incrementalConsistencyCheckingStage,
			incrementalTaxonomyCleaningStage,
			incrementalClassTaxonomyComputationStage,
			instanceTaxonomyComputationStage;

	ReasonerStageManager(AbstractReasonerState reasoner) {

		// Java will not allow to define stages with cyclic dependencies

		/* Non-Incremental stages */

		this.ontologyLoadingStage = new OntologyLoadingStage(reasoner);

		this.changesLoadingStage = new ChangesLoadingStage(reasoner,
				ontologyLoadingStage);

		this.propertyInitializationStage = new PropertyInitializationStage(
				reasoner, changesLoadingStage);

		this.propertyReflexivityComputationStage = new PropertyReflexivityComputationStage(
				reasoner, propertyInitializationStage);

		this.propertyHierarchyCompositionComputationStage = new PropertyHierarchyCompositionComputationStage(
				reasoner, propertyReflexivityComputationStage);

		this.contextInitializationStage = new ContextInitializationStage(
				reasoner, propertyHierarchyCompositionComputationStage);

		this.consistencyCheckingStage = new ConsistencyCheckingStage(reasoner,
				contextInitializationStage);

		this.classSaturationStage = new ClassSaturationStage(reasoner,
				consistencyCheckingStage);

		this.classTaxonomyComputationStage = new ClassTaxonomyComputationStage(
				reasoner, consistencyCheckingStage);

		/* Incremental stages */

		this.incrementalCompletionStage = new IncrementalCompletionStage(
				reasoner, propertyHierarchyCompositionComputationStage,
				changesLoadingStage);

		this.incrementalDeletionInitializationStage = new IncrementalDeletionInitializationStage(
				reasoner, incrementalCompletionStage);

		this.incrementalDeletionStage = new IncrementalDeletionStage(reasoner,
				incrementalDeletionInitializationStage);

		this.initializeContextsAfterDeletionsStage = new InitializeContextsAfterDeletionsStage(
				reasoner, incrementalDeletionStage);

		this.incrementalContextCleaningStage = new IncrementalContextCleaningStage(
				reasoner, initializeContextsAfterDeletionsStage);

		this.initializeContextsAfterCleaningStage = new InitializeContextsAfterCleaningStage(
				reasoner, incrementalContextCleaningStage);

		this.incrementalAdditionInitializationStage = new IncrementalAdditionInitializationStage(
				reasoner, initializeContextsAfterCleaningStage);

		this.incrementalAdditionStage = new IncrementalAdditionStage(reasoner,
				incrementalAdditionInitializationStage);

		this.incrementalConsistencyCheckingStage = new IncrementalConsistencyCheckingStage(
				reasoner, incrementalAdditionStage);

		this.incrementalTaxonomyCleaningStage = new IncrementalTaxonomyCleaningStage(
				reasoner, incrementalConsistencyCheckingStage);

		this.incrementalClassTaxonomyComputationStage = new IncrementalClassTaxonomyComputationStage(
				reasoner, incrementalTaxonomyCleaningStage);

		this.instanceTaxonomyComputationStage = new InstanceTaxonomyComputationStage(
				reasoner, classTaxonomyComputationStage);

	}
}
