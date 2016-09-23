/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owlapi.OwlApiIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;

@RunWith(PolySuite.class)
public class OwlApiIncrementalClassExpressionEquivalentClassesQueryTest extends
		OwlApiIncrementalClassExpressionQueryTest<EquivalentEntitiesTestOutput<OWLClass>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			"Inconsistent.owl",// Throwing InconsistentOntologyException
			"InconsistentInstances.owl",// Throwing InconsistentOntologyException
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final TestInput input) {
		return Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

	public OwlApiIncrementalClassExpressionEquivalentClassesQueryTest(
			final TestManifest<ClassQueryTestInput<OWLClassExpression>> manifest) {
		super(manifest,
				new OwlApiIncrementalReasoningTestDelegate<EquivalentEntitiesTestOutput<OWLClass>, EquivalentEntitiesTestOutput<OWLClass>>(
						manifest) {

					@Override
					public EquivalentEntitiesTestOutput<OWLClass> getExpectedOutput()
							throws Exception {
						final Node<OWLClass> equivalent = standardReasoner_
								.getEquivalentClasses(
										manifest.getInput().getClassQuery());
						return new OwlApiEquivalentEntitiesTestOutput(
								equivalent);
					}

					@Override
					public EquivalentEntitiesTestOutput<OWLClass> getActualOutput()
							throws Exception {
						final Node<OWLClass> equivalent = incrementalReasoner_
								.getEquivalentClasses(
										manifest.getInput().getClassQuery());
						return new OwlApiEquivalentEntitiesTestOutput(
								equivalent);
					}

				});
	}

}
