/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.reasoner.BaseClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.UrlTestInput;

@RunWith(PolySuite.class)
public class OWLAPIDiffClassificationCorrectnessTest
		extends BaseClassificationCorrectnessTest {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			// OWL API bug see here:
			// https://github.com/owlcs/owlapi/issues/151
			// TODO: this seems to have been fixed in OWL API 5
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/DisjointSelf.owl",
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public OWLAPIDiffClassificationCorrectnessTest(
			final ReasoningTestManifest<ClassTaxonomyTestOutput> testManifest) {
		super(testManifest,
				new OwlApiReasoningTestDelegate<ClassTaxonomyTestOutput>(
						testManifest) {

					@Override
					public ClassTaxonomyTestOutput getActualOutput()
							throws Exception {
						return new ClassTaxonomyTestOutput(
								getReasoner().getInternalReasoner()); 
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ElkInterruptedException.class;
					}

				});
	}

	@Override
	protected boolean ignore(final UrlTestInput input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

}
