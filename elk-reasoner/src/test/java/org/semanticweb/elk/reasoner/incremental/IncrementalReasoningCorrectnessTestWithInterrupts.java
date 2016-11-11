/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.incremental;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;

public abstract class IncrementalReasoningCorrectnessTestWithInterrupts<I extends TestInput, A, EO extends TestOutput, AO extends TestOutput, TD extends IncrementalReasoningTestWithInterruptsDelegate<A, EO, AO>>
		extends BaseIncrementalReasoningCorrectnessTest<I, A, EO, AO, TD> {

	public IncrementalReasoningCorrectnessTestWithInterrupts(
			final TestManifest<I> testManifest, final TD testDelegate) {
		super(testManifest, testDelegate);
	}

	@Test
	public void completingIncrementalReasoningWithInterrupts()
			throws Exception {
		load();

		delegate_.initWithInterrupts();

		run(new CompletingCheckerWithInterrupts());
	}

	protected class CompletingCheckerWithInterrupts extends OutputChecker {

		@Override
		public void check() throws Exception {
			finalCheck();
		}

		@Override
		public void finalCheck() throws Exception {
			AO actualOutput;
			while (true) {
				try {
					actualOutput = delegate_.getActualOutput();
				} catch (final Exception e) {
					if (delegate_.getInterruptionExceptionClass()
							.isInstance(e)) {
						continue;
					}
					throw e;
				}
				break;
			}
			correctnessCheck(actualOutput, delegate_.getExpectedOutput());
		}

	}

	@Test
	@Ignore
	public void incrementalReasoningWithInterrupts() throws Exception {
		load();

		delegate_.initWithInterrupts();

		run(new NoncompletingCheckerWithInterrupts());
	}

	protected class NoncompletingCheckerWithInterrupts
			extends CompletingCheckerWithInterrupts {

		@Override
		public void check() throws Exception {
			AO actualOutput;
			try {
				actualOutput = delegate_.getActualOutput();
			} catch (final Exception e) {
				if (delegate_.getInterruptionExceptionClass().isInstance(e)) {
					return;
				}
				throw e;
			}
			correctnessCheck(actualOutput, delegate_.getExpectedOutput());
		}

	}

}
