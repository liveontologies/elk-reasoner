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

import org.junit.runner.RunWith;
import org.semanticweb.elk.reasoner.BaseObjectPropertyClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.ObjectPropertyTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.testing.PolySuite;

/**
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class OWLAPIDiffObjectPropertyClassificationCorrectnessTest
		extends BaseObjectPropertyClassificationCorrectnessTest {

	public OWLAPIDiffObjectPropertyClassificationCorrectnessTest(
			final ReasoningTestManifest<ObjectPropertyTaxonomyTestOutput> testManifest) {
		super(testManifest,
				new OwlApiReasoningTestDelegate<ObjectPropertyTaxonomyTestOutput>(
						testManifest) {

					@Override
					public ObjectPropertyTaxonomyTestOutput getActualOutput()
							throws Exception {
						return new ObjectPropertyTaxonomyTestOutput(
								getReasoner().getInternalReasoner());
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ElkInterruptedException.class;
					}

				});
	}

}
