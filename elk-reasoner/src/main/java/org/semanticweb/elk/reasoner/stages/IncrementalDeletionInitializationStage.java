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
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
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
		ChainableRule<Context> changedInitRules = null;
		Map<IndexedClassExpression, ChainableRule<Context>> changedRulesByCE = null;
		Collection<Collection<Context>> inputs = Collections.emptyList();

		changedInitRules = diffIndex.getRemovedContextInitRules();
		changedRulesByCE = diffIndex.getRemovedContextRulesByClassExpressions();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()) {

			inputs = Operations.split(reasoner.saturationState.getContexts(),
					128);
		}
		
		//System.err.println(changedRulesByCE.keySet().size());

		this.initialization_ = new IncrementalChangesInitialization(inputs,
				changedInitRules, changedRulesByCE, reasoner.saturationState,
				reasoner.getProcessExecutor(), stageStatistics_, workerNo,
				reasoner.getProgressMonitor());
		
		return true;
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		this.initialization_ = null;
		// initializing contexts which will be removed
		final ConclusionVisitor<?> conclusionVisitor = SaturationUtils.addStatsToConclusionVisitor(stageStatistics_.getConclusionStatistics());
		final ExtendedSaturationStateWriter satStateWriter = reasoner.saturationState.getExtendedWriter(conclusionVisitor);
		final ClassTaxonomyState.Writer taxStateWriter = reasoner.classTaxonomyState.getWriter();
		final InstanceTaxonomyState.Writer instanceTaxStateWriter = reasoner.instanceTaxonomyState.getWriter();
		final IndexedClassExpressionVisitor<Object> rootVisitor = new AbstractIndexedClassEntityVisitor<Object>() {

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
		
		for (IndexedClassExpression ice : reasoner.ontologyIndex.getRemovedClassExpressions()) {

			if (ice.getContext() != null) {
				satStateWriter.initContext(ice.getContext());
				//otherwise it may not be cleaned
				//we could use writer.markContextAsNotSaturated
				//but then the context will end up in the queue
				//for not saturated contexts, which isn't needed here
				ice.getContext().setSaturated(false);
				ice.getContext().getRoot().accept(rootVisitor);
			}
		}

		reasoner.ontologyIndex.clearDeletedRules();

		return true;
	}

}