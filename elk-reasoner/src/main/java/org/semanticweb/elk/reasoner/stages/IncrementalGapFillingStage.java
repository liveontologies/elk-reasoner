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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCompletionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * FIXME Better name
 * 
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalGapFillingStage extends AbstractReasonerStage {

	private ClassExpressionSaturation<IndexedClassExpression> completion_;

	public IncrementalGapFillingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Incremental Gap Filling";
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		RuleApplicationFactory ruleAppFactory = new ContextCompletionFactory(
				reasoner.saturationState);
		Collection<IndexedClassExpression> inputs = reasoner.saturationState
				.getNotSaturatedContexts();

		completion_ = new ClassExpressionSaturation<IndexedClassExpression>(
				inputs, reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), ruleAppFactory);

		return true;
	}

	@Override
	void executeStage() throws ElkException {
		for (;;) {
			completion_.process();

			if (!spuriousInterrupt())
				break;
		}
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}

		completion_ = null;

		return true;
	}

}
