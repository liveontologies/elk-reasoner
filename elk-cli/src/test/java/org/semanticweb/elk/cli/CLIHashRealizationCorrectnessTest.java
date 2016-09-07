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
/**
 * 
 */
package org.semanticweb.elk.cli;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.HashRealizationCorrectnessTest;
import org.semanticweb.elk.reasoner.InstanceTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.HashTestOutput;

/**
 * Loads test ontologies using Elk's native OWL 2 functional syntax parser
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */

public class CLIHashRealizationCorrectnessTest extends
		HashRealizationCorrectnessTest {

	public CLIHashRealizationCorrectnessTest(
			final ReasoningTestManifest<HashTestOutput, InstanceTaxonomyTestOutput<?>> testManifest) {
		super(testManifest,
				new CliReasoningTestDelegate<InstanceTaxonomyTestOutput<?>>(
						testManifest) {

					@Override
					public InstanceTaxonomyTestOutput<?> getActualOutput()
							throws Exception {
						final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner_
								.getInstanceTaxonomyQuietly();
						return new InstanceTaxonomyTestOutput<InstanceTaxonomy<ElkClass, ElkNamedIndividual>>(
								taxonomy);
					}

				});
	}

}