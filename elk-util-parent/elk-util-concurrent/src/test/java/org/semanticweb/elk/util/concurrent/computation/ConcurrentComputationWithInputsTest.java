/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.util.concurrent.computation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

public class ConcurrentComputationWithInputsTest {

	private static int MAX_INPUT = 1000;

	private static int MAX_JOBS = 200;

	private static int ROUNDS_ = 200;

	private static final double INTERRUPTION_CHANCE = 0.15;
	private static final long INTERRUPTION_INTERVAL_NANOS = 10000l;

	private final Random random = new Random();

	private TestInputProcessorFactory factory_;

	private ConcurrentComputationWithInputs<Integer, ?> computation_;

	private final ConcurrentExecutor executor = ConcurrentExecutors
			.create("test-worker");

	void setup(int round, final InterruptMonitor interrupter) {
		int workers = random.nextInt(round + 1) + 1;
		factory_ = new TestInputProcessorFactory(interrupter);
		computation_ = new ConcurrentComputationWithInputs<Integer, TestInputProcessorFactory>(
				factory_, executor, workers, workers);
	}

	@Test
	public void test() {
		run(new TestInterrupter());
	}

	public void run(final TestInterrupter interrupter) {

		int jobs = 1;
		for (int round = 0; round < ROUNDS_; round++) {
			setup(round, interrupter);
			jobs = random.nextInt(MAX_JOBS);
			int sumExpected = 0;
			if (!computation_.start())
				fail();
			try {
				for (int j = 0; j < jobs; j++) {
					int nextInput = random.nextInt(MAX_INPUT) + 1;
					sumExpected += nextInput;
					for (;;) {
						if (computation_.submit(nextInput))
							break;
						// else must be interrupted
						if (!computation_.isInterrupted())
							fail();
						computation_.finish();
						// restart computation
						interrupter.clearInterrupt();
						if (!computation_.start())
							fail();
					}
				}
				for (;;) {
					computation_.finish();
					if (!computation_.isInterrupted()) {
						break;
					}
					// else restart
					interrupter.clearInterrupt();
					if (!computation_.start()) {
						fail();
					}
				}
			} catch (InterruptedException fail) {
				fail();
			}
			assertEquals(sumExpected, factory_.getSum());
		}

	}

	@Test
	public void testWithInterrupts() {
		run(new TestInterrupter(new RandomInterruptMonitor(random,
				INTERRUPTION_CHANCE, INTERRUPTION_INTERVAL_NANOS)));
	}

}
