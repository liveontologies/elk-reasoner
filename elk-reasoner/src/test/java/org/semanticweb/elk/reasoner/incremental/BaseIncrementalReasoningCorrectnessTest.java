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
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.BaseReasoningCorrectnessTest;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
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
public abstract class BaseIncrementalReasoningCorrectnessTest<I extends TestInput, A, EO extends TestOutput, AO extends TestOutput, TD extends IncrementalReasoningTestDelegate<A, EO, AO>>
		extends BaseReasoningCorrectnessTest<I, AO, TestManifest<I>, TD> {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(BaseIncrementalReasoningCorrectnessTest.class);

	final static int REPEAT_NUMBER = 5;
	final static double DELETE_RATIO = 0.2;

	protected OnOffVector<A> changingAxioms = null;

	public BaseIncrementalReasoningCorrectnessTest(
			final TestManifest<I> testManifest, final TD testDelegate) {
		super(testManifest, testDelegate);
	}

	protected void load() throws Exception {
		changingAxioms = new OnOffVector<A>(15);
		changingAxioms.addAll(delegate_.load());
		changingAxioms.setAllOn();
	}

	/**
	 * The main test method
	 * 
	 * @throws ElkException
	 */
	@Test
	public void incrementalReasoning() throws Exception {
		LOGGER_.debug("incrementalReasoning({})", manifest.getName());
		load();

		delegate_.initIncremental();

		run(new OutputChecker());
	}

	public void run(final OutputChecker outputChecker) throws Exception {

		final long seed = RandomSeedProvider.VALUE;
		final Random rnd = new Random(seed);

		try {

			for (int i = 0; i < REPEAT_NUMBER; i++) {

				outputChecker.check();

				changingAxioms.setAllOff();
				// delete some axioms

				randomFlip(changingAxioms, rnd, DELETE_RATIO);

				if (LOGGER_.isDebugEnabled()) {
					LOGGER_.debug("Round {} of {}", i+1, REPEAT_NUMBER);
					for (A del : changingAxioms.getOnElements()) {
						delegate_.dumpChangeToLog(del, LOGGER_, LogLevel.DEBUG);
					}
				}

				// incremental changes
				delegate_.applyChanges(changingAxioms.getOnElements(),
						IncrementalChangeType.DELETE);

				LOGGER_.info("===DELETIONS===");

				outputChecker.check();

				// add the axioms back
				delegate_.applyChanges(changingAxioms.getOnElements(),
						IncrementalChangeType.ADD);

				LOGGER_.info("===ADDITIONS===");
			}

			outputChecker.finalCheck();

		} catch (final Throwable e) {
			throw new RuntimeException("Random seed: " + seed, e);
		}

	}

	protected class OutputChecker {

		public void check() throws Exception {
			correctnessCheck(delegate_.getActualOutput(),
					delegate_.getExpectedOutput());
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

	protected abstract void correctnessCheck(AO actualOutput, EO expectedOutput)
			throws Exception;

}
