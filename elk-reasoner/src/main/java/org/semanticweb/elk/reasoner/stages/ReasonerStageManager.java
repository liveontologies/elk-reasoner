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
package org.semanticweb.elk.reasoner.stages;

/**
 * Defines all {@link ReasonerStage}s used by the reasoner and dependencies
 * between them.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class ReasonerStageManager {

	/* 
	 * TODO: This is a temporary solution so that the query loading has access
	 * to the information from the loading stage. Ideally, query loading should
	 * be done during the loading stage.
	 */
	final AxiomLoadingStage axiomLoadingStage;
	final AbstractReasonerStage propertyInitializationStage,
			propertyHierarchyCompositionComputationStage,
			objectPropertyTaxonomyComputationStage, contextInitializationStage,
			consistencyCheckingStage, classSaturationStage,
			classTaxonomyComputationStage, incrementalCompletionStage,
			incrementalDeletionInitializationStage, incrementalDeletionStage,
			incrementalContextGapFillingStage,
			incrementalAdditionInitializationStage, incrementalAdditionStage,
			incrementalConsistencyCheckingStage,
			incrementalTaxonomyCleaningStage,
			incrementalClassTaxonomyComputationStage,
			instanceTaxonomyComputationStage,
			incrementalInstanceTaxonomyComputationStage, inferenceTracingStage,
			classExpressionQueryStage;

	ReasonerStageManager(AbstractReasonerState reasoner) {

		// Java will not allow to define stages with cyclic dependencies

		/* Non-Incremental stages */

		this.axiomLoadingStage = new AxiomLoadingStage(reasoner);

		this.propertyInitializationStage = new PropertyInitializationStage(
				reasoner);

		this.propertyHierarchyCompositionComputationStage = new PropertyHierarchyCompositionComputationStage(
				reasoner, propertyInitializationStage);

		this.objectPropertyTaxonomyComputationStage = reasoner.objectPropertyTaxonomyState
				.createStage(reasoner,
						propertyHierarchyCompositionComputationStage);

		this.contextInitializationStage = new ContextAssignmentResetStage(
				reasoner, axiomLoadingStage,
				propertyHierarchyCompositionComputationStage);

		this.consistencyCheckingStage = new ConsistencyCheckingStage(reasoner,
				axiomLoadingStage, contextInitializationStage);

		this.classSaturationStage = new ClassSaturationStage(reasoner,
				consistencyCheckingStage);

		this.classTaxonomyComputationStage = new ClassTaxonomyComputationStage(
				reasoner, consistencyCheckingStage);

		this.instanceTaxonomyComputationStage = new InstanceTaxonomyComputationStage(
				reasoner, classTaxonomyComputationStage);

		/* Incremental stages */

		this.incrementalCompletionStage = new IncrementalCompletionStage(
				reasoner, axiomLoadingStage,
				propertyHierarchyCompositionComputationStage);

		this.incrementalDeletionInitializationStage = new IncrementalDeletionInitializationStage(
				reasoner, incrementalCompletionStage);

		this.incrementalDeletionStage = new IncrementalDeletionStage(reasoner,
				incrementalDeletionInitializationStage);

		this.incrementalContextGapFillingStage = new IncrementalOverdeletionPruningStage(
				reasoner, incrementalDeletionStage);

		this.incrementalAdditionInitializationStage = new IncrementalAdditionInitializationStage(
				reasoner, incrementalContextGapFillingStage/* initializeContextsAfterCleaningStage */);

		this.incrementalAdditionStage = new IncrementalAdditionStage(reasoner,
				incrementalAdditionInitializationStage);

		this.incrementalTaxonomyCleaningStage = new IncrementalTaxonomyCleaningStage(
				reasoner, incrementalAdditionStage);

		this.incrementalConsistencyCheckingStage = new IncrementalConsistencyCheckingStage(
				reasoner, incrementalTaxonomyCleaningStage);

		this.incrementalClassTaxonomyComputationStage = new IncrementalClassTaxonomyComputationStage(
				reasoner, incrementalConsistencyCheckingStage);

		this.incrementalInstanceTaxonomyComputationStage = new IncrementalInstanceTaxonomyComputationStage(
				reasoner, incrementalClassTaxonomyComputationStage);

		/* Tracing stages */

		this.inferenceTracingStage = new InferenceTracingStage(reasoner);

		/* Query stages */

		this.classExpressionQueryStage = new ClassExpressionQueryStage(
				reasoner);

	}
}
