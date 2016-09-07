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
package org.semanticweb.elk.cli.query;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.incremental.CliIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;

@RunWith(PolySuite.class)
public class CliIncrementalClassExpressionEquivalentClassesQueryTest extends
		CliIncrementalClassExpressionQueryTest<EquivalentEntitiesTestOutput<ElkClass>> {

	public CliIncrementalClassExpressionEquivalentClassesQueryTest(
			final TestManifest<ClassQueryTestInput<ElkClassExpression>> manifest) {
		super(manifest,
				new CliIncrementalReasoningTestDelegate<EquivalentEntitiesTestOutput<ElkClass>, EquivalentEntitiesTestOutput<ElkClass>>(
						manifest) {

					@Override
					public EquivalentEntitiesTestOutput<ElkClass> getExpectedOutput()
							throws Exception {
						final Node<ElkClass> equivalent = standardReasoner_
								.getEquivalentClassesQuietly(
										manifest.getInput().getClassQuery());
						return new CliEquivalentEntitiesTestOutput(equivalent);
					}

					@Override
					public EquivalentEntitiesTestOutput<ElkClass> getActualOutput()
							throws Exception {
						final Node<ElkClass> equivalent = incrementalReasoner_
								.getEquivalentClassesQuietly(
										manifest.getInput().getClassQuery());
						return new CliEquivalentEntitiesTestOutput(equivalent);
					}

				});
	}

}
