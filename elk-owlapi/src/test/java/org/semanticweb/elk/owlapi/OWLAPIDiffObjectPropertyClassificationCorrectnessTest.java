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
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.DiffObjectPropertyClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;

/**
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class OWLAPIDiffObjectPropertyClassificationCorrectnessTest
		extends DiffObjectPropertyClassificationCorrectnessTest {

	static final String[] IGNORE_LIST = {};

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public OWLAPIDiffObjectPropertyClassificationCorrectnessTest(
			final ReasoningTestManifest<TaxonomyTestOutput<?>, TaxonomyTestOutput<?>> testManifest) {
		super(testManifest,
				new OwlApiReasoningTestDelegate<TaxonomyTestOutput<?>>(
						testManifest) {

					@Override
					public TaxonomyTestOutput<?> getActualOutput()
							throws Exception {
						final Taxonomy<ElkObjectProperty> taxonomy = reasoner_
								.getInternalReasoner()
								.getObjectPropertyTaxonomyQuietly();
						return new TaxonomyTestOutput<Taxonomy<ElkObjectProperty>>(
								taxonomy);
					}

				});
	}

	@Override
	protected boolean ignore(TestInput input) {
		return super.ignore(input)
				|| Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

}
