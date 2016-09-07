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

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.runner.RunWith;
import org.semanticweb.elk.testing.HashTestOutput;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public abstract class HashRealizationCorrectnessTest extends
		BaseRealizationCorrectnessTest<HashTestOutput> {

	public HashRealizationCorrectnessTest(
			final ReasoningTestManifest<HashTestOutput, InstanceTaxonomyTestOutput<?>> testManifest,
			final ReasoningTestDelegate<InstanceTaxonomyTestOutput<?>> testDelegate) {
		super(testManifest, testDelegate);
	}

	/*
	 * --------------------------------------------- Configuration: loading all
	 * test input data ---------------------------------------------
	 */
	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return HashConfigurationUtils
				.<InstanceTaxonomyTestOutput<?>> loadConfiguration(INPUT_DATA_LOCATION);
	}
}
