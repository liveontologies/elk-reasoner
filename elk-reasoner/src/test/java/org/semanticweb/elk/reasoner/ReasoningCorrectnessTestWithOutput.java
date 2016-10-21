/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.TestOutput;

/**
 * Runs tests for all pairs of test input and expected output in the test
 * directory.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 * @param <EO>
 *            The type of expected test output.
 * @param <AO>
 *            The type of actual test output.
 */
@RunWith(PolySuite.class)
public abstract class ReasoningCorrectnessTestWithOutput<I extends TestInput, EO extends TestOutput, AO extends TestOutput, TM extends TestManifestWithOutput<I, EO, AO>, TD extends ReasoningTestWithOutputDelegate<AO>>
		extends BaseReasoningCorrectnessTest<I, AO, TM, TD> {

	public ReasoningCorrectnessTestWithOutput(final TM testManifest,
			final TD testDelegate) {
		super(testManifest, testDelegate);
	}

	/**
	 * Checks that the actual test output is the same as the expected output.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		delegate_.initWithOutput();
		final AO actualOutput = delegate_.getActualOutput();
		manifest.compare(actualOutput);
	}

}