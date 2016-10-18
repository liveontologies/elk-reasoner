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

import static org.junit.Assert.fail;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TestInputProcessorFactory extends DelegateInterruptMonitor
		implements
		InputProcessorFactory<Integer, TestInputProcessorFactory.Engene> {

	private final int seepInterval_, sleepIntervalDelta_;

	private final Queue<Integer> todo_ = new ConcurrentLinkedQueue<Integer>();

	private final AtomicInteger aggregatedSum_ = new AtomicInteger(0);

	public TestInputProcessorFactory(final InterruptMonitor interrupter,
			int sleepInterval, int sleepIntervalDelta) {
		super(interrupter);
		this.seepInterval_ = sleepInterval;
		this.sleepIntervalDelta_ = sleepIntervalDelta;
	}

	@Override
	public TestInputProcessorFactory.Engene getEngine() {
		return new Engene();
	}

	@Override
	public void finish() {
		// nothing to do
	}

	public int getSum() {
		return aggregatedSum_.get();
	}

	class Engene implements InputProcessor<Integer> {

		private int sum_ = 0;

		private final Random random = new Random();

		private int sleepCountdown_ = 0;

		@Override
		public void submit(Integer job) {
			todo_.add(job);
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (isInterrupted())
					return;
				Integer nextInput = todo_.poll();
				if (nextInput == null)
					return;
				sum_ += nextInput;
				sleepCountdown_ -= nextInput * sleepIntervalDelta_;
				while (sleepCountdown_ <= 0) {
					sleepCountdown_ += random.nextInt(seepInterval_) + 1;
					Thread.sleep(1);
				}
			}
		}

		@Override
		public void finish() {
			aggregatedSum_.addAndGet(sum_);
			sum_ = 0;
			if (!isInterrupted() && !todo_.isEmpty())
				fail();
		}

	}

}
