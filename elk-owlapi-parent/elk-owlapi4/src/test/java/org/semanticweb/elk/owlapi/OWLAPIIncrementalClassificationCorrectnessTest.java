/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.incremental.BaseIncrementalClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class OWLAPIIncrementalClassificationCorrectnessTest
		extends BaseIncrementalClassificationCorrectnessTest<OWLAxiom> {

	public OWLAPIIncrementalClassificationCorrectnessTest(
			final TestManifest<UrlTestInput> testManifest) {
		super(testManifest,
				new OwlApiIncrementalReasoningTestDelegate<ClassTaxonomyTestOutput>(
						testManifest) {

					@Override
					public ClassTaxonomyTestOutput getExpectedOutput()
							throws Exception {
						LOGGER_.trace(
								"======= Computing Expected Taxonomy =======");						
						return new ClassTaxonomyTestOutput(
								getStandardReasoner().getInternalReasoner());
					}

					@Override
					public ClassTaxonomyTestOutput getActualOutput()
							throws Exception {
						LOGGER_.trace(
								"======= Computing Incremental Taxonomy =======");
						return new ClassTaxonomyTestOutput(
								getIncrementalReasoner().getInternalReasoner());
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ElkInterruptedException.class;
					}

				});
	}

}
