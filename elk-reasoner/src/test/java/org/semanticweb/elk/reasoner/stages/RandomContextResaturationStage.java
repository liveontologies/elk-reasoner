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
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationNoInput;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationDeletionNotSaturatedFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomContextResaturationStage extends AbstractReasonerStage {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(RandomContextResaturationStage.class);

	private final double RATIO_ = 0.2;

	public RandomContextResaturationStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Clean and re-saturate random contexts";
	}

	@Override
	public void executeStage() throws ElkException {
		// TODO: it seems like this stage cannot be currently interrupted

		// first, pick random class expressions
		List<IndexedClassExpression> contexts = pickRandomContexts(
				RandomSeedProvider.VALUE);
		// init them for deletions
		initContexts(contexts);
		// and now clean then up
		RuleApplicationFactory<?, RuleApplicationInput> cleaningFactory = new RuleApplicationDeletionNotSaturatedFactory(
				reasoner.getInterrupter(), reasoner.saturationState);

		LOGGER_.trace("Starting random contexts cleaning");

		ClassExpressionSaturationNoInput cleaning = new ClassExpressionSaturationNoInput(
				reasoner.getProcessExecutor(), reasoner.getNumberOfWorkers(),
				cleaningFactory, ContextModificationListener.DUMMY);

		cleaning.process();

		initContexts(contexts);
		// re-saturate
		RuleApplicationAdditionFactory<RuleApplicationInput> resatFactory = new RuleApplicationAdditionFactory<RuleApplicationInput>(
				reasoner.getInterrupter(), reasoner.saturationState);

		ClassExpressionSaturationNoInput saturation = new ClassExpressionSaturationNoInput(
				reasoner.getProcessExecutor(), reasoner.getNumberOfWorkers(),
				resatFactory, ContextModificationListener.DUMMY);

		LOGGER_.trace("Starting random contexts resaturation");

		saturation.process();

		markAllContextsAsSaturated();
	}

	private void initContexts(Collection<IndexedClassExpression> roots) {
		SaturationStateWriter<?> writer = reasoner.saturationState
				.getContextCreatingWriter();
		for (IndexedClassExpression root : roots) {
			if (reasoner.saturationState.getContext(root) != null)
				writer.produce(new ContextInitializationNoPremises(root));
		}
	}

	private List<IndexedClassExpression> pickRandomContexts(long seed) {
		Random rnd = new Random(seed);
		List<IndexedClassExpression> contexts = new ArrayList<IndexedClassExpression>();
		Collection<? extends IndexedClassExpression> ices = reasoner.ontologyIndex
				.getClassExpressions();
		int number = Math.max(1, (int) (ices.size() * RATIO_));
		Set<Integer> indexes = new ArrayHashSet<Integer>(number);

		while (indexes.size() < number) {
			indexes.add(rnd.nextInt(ices.size()));
		}

		int i = 0;

		for (IndexedClassExpression ice : ices) {
			SaturationStateWriter<?> writer = reasoner.saturationState
					.getContextModifyingWriter();
			if (indexes.contains(i)) {
				writer.markAsNotSaturated(ice);
				contexts.add(ice);
			}
			i++;
		}

		LOGGER_.trace("Random contexts picked: {}", contexts);

		return contexts;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

	@Override
	public Iterable<ReasonerStage> getPreStages() {
		return Collections.emptyList();
	}

	@Override
	public void printInfo() {
		// nothing
	}

}
