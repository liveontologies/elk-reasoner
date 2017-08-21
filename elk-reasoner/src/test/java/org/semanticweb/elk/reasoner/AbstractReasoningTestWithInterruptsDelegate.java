/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.UrlTestInput;

public abstract class AbstractReasoningTestWithInterruptsDelegate<O>
		implements ReasoningTestWithInterruptsDelegate<O> {

	public static final double DEFAULT_INTERRUPTION_CHANCE = 0.15;

	public static final long DEFAULT_INTERRUPTION_INTERVAL_NANOS = 10000;

	private final TestManifest<? extends UrlTestInput> manifest_;

	private final double interruptionChance_;

	private final long interruptionIntervalNanos_;

	public AbstractReasoningTestWithInterruptsDelegate(
			final TestManifest<? extends UrlTestInput> manifest,
			final double interruptionChance,
			final long interruptionIntervalNanos) {
		this.manifest_ = manifest;
		this.interruptionChance_ = interruptionChance;
		this.interruptionIntervalNanos_ = interruptionIntervalNanos;
	}

	public AbstractReasoningTestWithInterruptsDelegate(
			final TestManifest<? extends UrlTestInput> manifest,
			final double interruptionChance) {
		this(manifest, interruptionChance, DEFAULT_INTERRUPTION_INTERVAL_NANOS);
	}

	public AbstractReasoningTestWithInterruptsDelegate(
			final TestManifest<? extends UrlTestInput> manifest) {
		this(manifest, DEFAULT_INTERRUPTION_CHANCE);
	}

	public TestManifest<? extends UrlTestInput> getManifest() {
		return manifest_;
	}

	@Override
	public double getInterruptionChance() {
		return interruptionChance_;
	}

	@Override
	public long getInterruptionIntervalNanos() {
		return interruptionIntervalNanos_;
	}

}
