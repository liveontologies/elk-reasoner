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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.DiffClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;

@RunWith(PolySuite.class)
public class OWLAPIDiffClassificationCorrectnessTest extends
		DiffClassificationCorrectnessTest {

	static final String[] IGNORE_LIST = { "DisjointSelf.owl", "CompositionReflexivityComplex.owl"/*,
			"AssertionDisjoint.owl", "Disjoint.owl", "ReflexiveRole.owl",
			"kangaroo.owl"*/ };

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public OWLAPIDiffClassificationCorrectnessTest(
			final ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	@Override
	protected Reasoner createReasoner(InputStream input) throws IOException,
			Owl2ParseException {
		return OWLAPITestUtils.createReasoner(input).getInternalReasoner();
	}

	@Override
	protected boolean ignore(TestInput input) {
		return Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

}