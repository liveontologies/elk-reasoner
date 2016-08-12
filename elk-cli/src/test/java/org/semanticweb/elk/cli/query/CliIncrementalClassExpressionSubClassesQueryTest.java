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

import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.incremental.CliIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;

@RunWith(PolySuite.class)
public class CliIncrementalClassExpressionSubClassesQueryTest extends
		CliIncrementalClassExpressionQueryTest<RelatedEntitiesTestOutput<ElkClass>> {

	public CliIncrementalClassExpressionSubClassesQueryTest(
			final TestManifest<ClassQueryTestInput<ElkClassExpression>> manifest) {
		super(manifest,
				new CliIncrementalReasoningTestDelegate<RelatedEntitiesTestOutput<ElkClass>, RelatedEntitiesTestOutput<ElkClass>>(
						manifest) {

					@Override
					public RelatedEntitiesTestOutput<ElkClass> getExpectedOutput()
							throws Exception {
						final Set<? extends Node<ElkClass>> subNodes = standardReasoner_
								.getSubClassesQuietly(
										manifest.getInput().getClassQuery(),
										true);
						return new CliRelatedEntitiesTestOutput(subNodes);
					}

					@Override
					public RelatedEntitiesTestOutput<ElkClass> getActualOutput()
							throws Exception {
						final Set<? extends Node<ElkClass>> subNodes = incrementalReasoner_
								.getSubClassesQuietly(
										manifest.getInput().getClassQuery(),
										true);
						return new CliRelatedEntitiesTestOutput(subNodes);
					}

				});
	}

}
