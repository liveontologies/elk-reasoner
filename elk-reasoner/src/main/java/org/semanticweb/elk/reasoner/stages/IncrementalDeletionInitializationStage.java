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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.visitors.AbstractIndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.Operations;

/**
 * 
 * 
 */
public class IncrementalDeletionInitializationStage extends
		AbstractIncrementalChangesInitializationStage {

	public IncrementalDeletionInitializationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.DELETIONS_INIT;
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;

		DifferentialIndex diffIndex = reasoner.ontologyIndex;
		LinkedContextInitRule changedInitRules = null;
		Map<IndexedClassExpression, ChainableSubsumerRule> changedRulesByCE = null;
		Map<IndexedClass, IndexedClassExpression> changedDefinitions = null;
		Collection<ArrayList<Context>> inputs = Collections.emptyList();

		changedInitRules = diffIndex.getRemovedContextInitRules();
		changedRulesByCE = diffIndex.getRemovedContextRulesByClassExpressions();
		changedDefinitions = diffIndex.getRemovedDefinitions();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()
				|| !changedDefinitions.isEmpty()) {

			inputs = Operations.split(reasoner.saturationState.getContexts(),
					8 * workerNo);
		}

		// System.err.println(changedRulesByCE.keySet().size());

		this.initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, changedDefinitions,
				reasoner.saturationState, reasoner.getProcessExecutor(),
				stageStatistics_, workerNo, reasoner.getProgressMonitor());

		return true;
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		this.initialization_ = null;
		// initializing contexts which will be removed
		final SaturationStateWriter<?> satStateWriter = SaturationUtils
				.getStatsAwareWriter(
						reasoner.saturationState.getContextCreatingWriter(),
						stageStatistics_);
		final ClassTaxonomyState.Writer taxStateWriter = reasoner.classTaxonomyState
				.getWriter();
		final InstanceTaxonomyState.Writer instanceTaxStateWriter = reasoner.instanceTaxonomyState
				.getWriter();
		final IndexedClassExpressionVisitor<Object> entityRemovalVisitor = new AbstractIndexedClassEntityVisitor<Object>() {

			@Override
			public Object visit(IndexedClass element) {
				taxStateWriter.markRemovedClass(element);
				return null;
			}

			@Override
			public Object visit(IndexedIndividual element) {
				instanceTaxStateWriter.markRemovedIndividual(element);
				return null;
			}
		};

		for (IndexedClassExpression ice : reasoner.ontologyIndex
				.getRemovedClassExpressions()) {

			Conclusion init = new ContextInitializationImpl(
					reasoner.saturationState.getOntologyIndex());

			if (reasoner.saturationState.getContext(ice) != null) {
				satStateWriter.produce(ice, init);
				// mark removed classes
				ice.accept(entityRemovalVisitor);
			}
		}

		reasoner.ontologyIndex.clearDeletedRules();

		return true;
	}
}
