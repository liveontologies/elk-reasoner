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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangesInitialization;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.classes.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.util.collections.Operations;

/**
 * 
 * 
 */
public class IncrementalAdditionInitializationStage
		extends
			AbstractIncrementalChangesInitializationStage {

	public IncrementalAdditionInitializationStage(
			AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	protected IncrementalStages stage() {
		return IncrementalStages.ADDITIONS_INIT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;

		DifferentialIndex diffIndex = reasoner.ontologyIndex;
		LinkedContextInitRule changedInitRules = null;
		Map<? extends IndexedClassExpression, ? extends LinkedSubsumerRule> changedRulesByCE = null;
		Map<? extends IndexedClass, ? extends IndexedClassExpression> changedDefinitions = null;
		Map<? extends IndexedClass, ? extends ElkAxiom> changedDefinitionReasons = null;
		Collection<ArrayList<Context>> inputs = Collections.emptyList();
		ContextCreationListener contextCreationListener = SaturationUtils
				.addStatsToContextCreationListener(
						ContextCreationListener.DUMMY,
						stageStatistics.getContextStatistics());
		ContextModificationListener contextModificationListener = SaturationUtils
				.addStatsToContextModificationListener(
						ContextModificationListener.DUMMY,
						stageStatistics.getContextStatistics());

		// first, create and init contexts for new classes
		final ElkPolarityExpressionConverter converter = new ElkPolarityExpressionConverterImpl(
				reasoner.getElkFactory(), reasoner.ontologyIndex);
		final SaturationStateWriter<?> writer = reasoner.saturationState
				.getContextCreatingWriter(contextCreationListener,
						contextModificationListener);

		for (ElkEntity newEntity : Operations.concat(
				reasoner.ontologyIndex.getAddedClasses(),
				reasoner.ontologyIndex.getAddedIndividuals())) {
			IndexedClassExpression ice = newEntity
					.accept(new ElkEntityVisitor<IndexedClassExpression>() {

						@Override
						public IndexedClassExpression visit(
								ElkAnnotationProperty elkAnnotationProperty) {
							return null;
						}

						@Override
						public IndexedClassExpression visit(ElkClass elkClass) {
							return converter.visit(elkClass);
						}

						@Override
						public IndexedClassExpression visit(
								ElkDataProperty elkDataProperty) {
							return null;
						}

						@Override
						public IndexedClassExpression visit(
								ElkDatatype elkDatatype) {
							return null;
						}

						@Override
						public IndexedClassExpression visit(
								ElkNamedIndividual elkNamedIndividual) {
							return converter.visit(elkNamedIndividual);
						}

						@Override
						public IndexedClassExpression visit(
								ElkObjectProperty elkObjectProperty) {
							return null;
						}
					});

			if (reasoner.saturationState.getContext(ice) == null)
				writer.produce(new ContextInitializationNoPremises(ice));
		}

		changedInitRules = diffIndex.getAddedContextInitRules();
		changedRulesByCE = diffIndex.getAddedContextRulesByClassExpressions();
		changedDefinitions = diffIndex.getAddedDefinitions();
		changedDefinitionReasons = diffIndex.getAddedDefinitionReasons();

		if (changedInitRules != null || !changedRulesByCE.isEmpty()
				|| !changedDefinitions.isEmpty()) {
			inputs = Operations.split(reasoner.saturationState.getContexts(),
					8 * workerNo);
		}

		this.initialization = new IncrementalChangesInitialization(inputs,
				reasoner.getInterrupter(),
				changedInitRules, changedRulesByCE, changedDefinitions,
				changedDefinitionReasons, reasoner.saturationState,
				reasoner.getProcessExecutor(), stageStatistics, workerNo,
				reasoner.getProgressMonitor());

		return true;
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ontologyIndex.commitAddedRules();
		reasoner.ontologyIndex.initClassChanges();
		reasoner.ontologyIndex.initIndividualChanges();
		return true;
	}

}
