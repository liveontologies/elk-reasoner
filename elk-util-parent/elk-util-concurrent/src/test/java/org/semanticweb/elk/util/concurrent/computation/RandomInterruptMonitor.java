/*-
 * #%L
 * ELK Utilities for Concurrency
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
package org.semanticweb.elk.util.concurrent.computation;

import java.util.Random;

/**
 * Behaves as if interruption happened randomly with the provided chance in the
 * provided intervals. If the time between two consecutive calls to
 * {@link #isInterrupted()} is <em>t</em>, then the probability that the second
 * one returns {@code true} is the probability that some of the trials happening
 * during this time was successful. The number of the trials is how many
 * intervals of the provided length fit into <em>t</em> and the probability that
 * a trial is successful is the provided chance.
 * 
 * @author Peter Skocovsky
 */
public class RandomInterruptMonitor implements InterruptMonitor {

	private final Random random_;
	private final double chance_;
	private final long intervalNanos_;

	private long lastTrialTimeNanos_;

	public RandomInterruptMonitor(final Random random, final double chance,
			final long intervalNanos) {
		if (chance < 0 || chance > 1) {
			throw new IllegalArgumentException(
					"chance must be between 0 and 1 inclusive!");
		}
		if (intervalNanos <= 0) {
			throw new IllegalArgumentException("interval must be positive!");
		}
		this.random_ = random;
		this.chance_ = chance;
		this.intervalNanos_ = intervalNanos;
		this.lastTrialTimeNanos_ = System.nanoTime();
	}

	@Override
	public synchronized boolean isInterrupted() {
		final long timeSinceLastTrial = System.nanoTime() - lastTrialTimeNanos_;
		final long nTrials = timeSinceLastTrial / intervalNanos_;
		if (nTrials > 0) {
			lastTrialTimeNanos_ += nTrials * intervalNanos_;
			final double chanceNow = 1 - Math.pow((1 - chance_), nTrials);
			if (random_.nextDouble() < chanceNow) {
				return true;
			}
		}
		// else
		return false;
	}

}
