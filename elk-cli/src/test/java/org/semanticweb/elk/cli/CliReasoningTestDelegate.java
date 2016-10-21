/*
 * #%L
 * ELK Command Line Interface
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.cli;

import java.util.Random;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.reasoner.RandomReasonerInterrupter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestWithInterruptsDelegate;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;

public abstract class CliReasoningTestDelegate<AO extends TestOutput>
		implements ReasoningTestWithInterruptsDelegate<AO> {

	public static final double INTERRUPTION_CHANCE = 0.3;

	protected final TestManifest<? extends UrlTestInput> manifest_;

	protected Reasoner reasoner_;

	public CliReasoningTestDelegate(
			final TestManifest<? extends UrlTestInput> manifest) {
		this.manifest_ = manifest;
	}

	@Override
	public void initWithOutput() throws Exception {
		reasoner_ = TestReasonerUtils.createTestReasoner(
				manifest_.getInput().getUrl().openStream(),
				new SimpleStageExecutor());
	}

	@Override
	public void initWithInterrupts() throws Exception {
		final Random random = new Random(RandomSeedProvider.VALUE);
		reasoner_ = TestReasonerUtils.createTestReasoner(
				manifest_.getInput().getUrl().openStream(),
				new RandomReasonerInterrupter(random, INTERRUPTION_CHANCE),
				new SimpleStageExecutor());
	}

	@Override
	public Class<? extends Exception> getInterruptionExceptionClass() {
		return ElkInterruptedException.class;
	}

	@Override
	public void before() throws Exception {
		// Empty.
	}

	@Override
	public void after() {
		// Empty.
	}

}
