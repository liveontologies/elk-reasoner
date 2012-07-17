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
/**
 * 
 */
package org.semanticweb.elk.reasoner;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestOutput;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @param <EO> 
 * @param <AO> 
 */
@RunWith(PolySuite.class)
public abstract class BaseReasoningCorrectnessTest<EO extends TestOutput, AO extends TestOutput> {

	protected final ReasoningTestManifest<EO, AO> manifest;
	private InputStream inputStream;
	protected Reasoner reasoner;

	public BaseReasoningCorrectnessTest(
			ReasoningTestManifest<EO, AO> testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));

		inputStream = manifest.getInput().getInputStream();
		reasoner = createReasoner(inputStream);
	}

	@After
	public void after() {
		IOUtils.closeQuietly(inputStream);
	}

	@SuppressWarnings("static-method")
	protected boolean ignore(TestInput input) {
		return false;
	}

	protected abstract Reasoner createReasoner(final InputStream input)
			throws IOException, Owl2ParseException;

}