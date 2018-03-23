/*
 * #%L
 * ELK Command Line Interface
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

import java.util.Arrays;

import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ElkDiffRealizationCorrectnessTest
		extends BaseRealizationCorrectnessTest {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/realization/AssertionsPropertyRanges.owl",
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public ElkDiffRealizationCorrectnessTest(
			final ReasoningTestManifest<InstanceTaxonomyTestOutput<?>> testManifest) {
		super(testManifest,
				new ElkReasoningTestDelegate<InstanceTaxonomyTestOutput<?>>(
						testManifest) {

					@Override
					public InstanceTaxonomyTestOutput<?> getActualOutput()
							throws Exception {
						final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = getReasoner()
								.getInstanceTaxonomyQuietly();
						return new InstanceTaxonomyTestOutput<InstanceTaxonomy<ElkClass, ElkNamedIndividual>>(
								taxonomy);
					}

				});
	}

	@Override
	protected boolean ignore(final UrlTestInput input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

}