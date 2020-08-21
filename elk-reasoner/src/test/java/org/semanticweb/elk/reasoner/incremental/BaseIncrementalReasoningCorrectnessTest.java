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
package org.semanticweb.elk.reasoner.incremental;

import java.util.Collections;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.reasoner.BaseReasoningCorrectnessTest;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.util.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public abstract class BaseIncrementalReasoningCorrectnessTest<I extends TestInput, A, O, TD extends IncrementalReasoningTestDelegate<A, O>>
		extends BaseReasoningCorrectnessTest<I, O, TestManifest<I>, TD> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(BaseIncrementalReasoningCorrectnessTest.class);

	private final static int REPEAT_NUMBER = 5;
	private final static double DELETE_RATIO = 0.2;

	private OnOffVector<A> changingAxioms_ = null;

	public BaseIncrementalReasoningCorrectnessTest(
			final TestManifest<I> testManifest, final TD testDelegate) {
		super(testManifest, testDelegate);
	}

	protected void load() throws Exception {
		changingAxioms_ = new OnOffVector<A>(15);
		changingAxioms_.addAll(getDelegate().load());
		changingAxioms_.setAllOn();
	}

	public OnOffVector<A> getChangingAxioms() {
		return changingAxioms_;
	}

	/**
	 * The main test method
	 * 
	 * @throws Exception
	 */
	@Test
	public void incrementalReasoning() throws Exception {
		LOGGER_.debug("incrementalReasoning({})", getManifest().getName());
		load();

		final Random random = new Random(RandomSeedProvider.VALUE);

		getDelegate().initIncremental();

		run(random, new OutputChecker());
	}

	public void run(final Random random, final OutputChecker outputChecker)
			throws Exception {

		try {

			for (int i = 0; i < REPEAT_NUMBER; i++) {

				outputChecker.check();

				changingAxioms_.setAllOff();
				// delete some axioms

				randomFlip(changingAxioms_, random, DELETE_RATIO);

				if (LOGGER_.isDebugEnabled()) {
					LOGGER_.debug("Round {} of {}", i + 1, REPEAT_NUMBER);
					for (A del : changingAxioms_.getOnElements()) {
						getDelegate().dumpChangeToLog(del, LOGGER_,
								LogLevel.DEBUG);
					}
				}

				// incremental changes
				getDelegate().applyChanges(changingAxioms_.getOnElements(),
						IncrementalChangeType.DELETE);

				LOGGER_.info("===DELETIONS===");

				outputChecker.check();

				// add the axioms back
				getDelegate().applyChanges(getChangingAxioms().getOnElements(),
						IncrementalChangeType.ADD);

				LOGGER_.info("===ADDITIONS===");
			}

			outputChecker.finalCheck();

		} catch (final Throwable e) {
			throw new RuntimeException(
					"Random seed: " + RandomSeedProvider.VALUE, e);
		}

	}

	protected class OutputChecker {

		public void check() throws Exception {
			correctnessCheck(getDelegate().getActualOutput(),
					getDelegate().getExpectedOutput());
		}

		public void finalCheck() throws Exception {
			check();
		}

	}

	private void randomFlip(OnOffVector<A> axioms, Random rnd,
			double fraction) {
		Collections.shuffle(axioms, rnd);

		int flipped = 0;

		for (int i = 0; i < axioms.size()
				&& flipped <= fraction * axioms.size(); i++) {
			axioms.flipOnOff(i);
			flipped++;
		}
	}

	protected abstract void correctnessCheck(O actualOutput, O expectedOutput)
			throws Exception;

}
