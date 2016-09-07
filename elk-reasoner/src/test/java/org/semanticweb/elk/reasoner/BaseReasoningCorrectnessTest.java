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

import static org.junit.Assume.assumeTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;

/**
 * Base class for reasoning tests that are run with {@link PolySuite}.
 * Subclasses of this class specify order of steps that are performed during a
 * tests, the test delegate passed to the constructor implements these steps,
 * and the test manifest passed to the constructor describes input and output of
 * the test.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 *
 * @param <I>
 *            The type of test input.
 * @param <AO>
 *            The type of actual test output.
 * @param <TM>
 *            The type of test manifest.
 * @param <TD>
 *            The type of test delegate.
 */
@RunWith(PolySuite.class)
public abstract class BaseReasoningCorrectnessTest<I extends TestInput, AO extends TestOutput, TM extends TestManifest<I>, TD extends ReasoningTestDelegate<AO>> {

	protected final TM manifest;
	protected final TD delegate_;

	public BaseReasoningCorrectnessTest(final TM testManifest,
			final TD testDelegate) {
		this.manifest = testManifest;
		this.delegate_ = testDelegate;
	}

	@Before
	public void before() throws Exception {
		assumeTrue(!ignore(manifest.getInput()));
		delegate_.init();
	}

	@After
	public void after() {
		delegate_.dispose();
	}

	protected boolean ignore(TestInput input) {
		return false;
	}

}