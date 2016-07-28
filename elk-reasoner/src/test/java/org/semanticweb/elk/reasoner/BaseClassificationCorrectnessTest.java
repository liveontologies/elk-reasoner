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

import org.junit.runner.RunWith;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * Runs classification tests for all test input in the test directory
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @param <EO>
 * 
 */
@RunWith(PolySuite.class)
public abstract class BaseClassificationCorrectnessTest<EO extends TestOutput>
		extends
		ReasoningCorrectnessTestWithInterrupts<UrlTestInput, EO, TaxonomyTestOutput<?>, ReasoningTestManifest<EO, TaxonomyTestOutput<?>>, ReasoningTestWithInterruptsDelegate<TaxonomyTestOutput<?>>> {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	public BaseClassificationCorrectnessTest(
			final ReasoningTestManifest<EO, TaxonomyTestOutput<?>> testManifest,
			final ReasoningTestWithInterruptsDelegate<TaxonomyTestOutput<?>> testDelegate) {
		super(testManifest, testDelegate);
	}

}