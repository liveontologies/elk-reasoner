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

import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

public class RandomInterruptMonitor implements InterruptMonitor {

	private final Interrupter interrupter_;
	private final Random random_;
	private final double chance_;

	public RandomInterruptMonitor(final Interrupter interrupter,
			final Random random, final double chance) {
		if (chance < 0 || chance > 1) {
			throw new IllegalArgumentException(
					"chance must be between 0 and 1 inclusive!");
		}
		this.interrupter_ = interrupter;
		this.random_ = random;
		this.chance_ = chance;
	}

	@Override
	public boolean isInterrupted() {
		if (random_.nextDouble() < chance_) {
			interrupter_.interrupt();
		}
		return interrupter_.isInterrupted();
	}

}
