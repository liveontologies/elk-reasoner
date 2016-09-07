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
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.incremental.CliIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseSatisfiabilityTestOutput;
import org.semanticweb.elk.reasoner.query.ClassQueryTestInput;
import org.semanticweb.elk.reasoner.query.SatisfiabilityTestOutput;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;

@RunWith(PolySuite.class)
public class CliIncrementalClassExpressionSatisfiabilityQueryTest extends
		CliIncrementalClassExpressionQueryTest<SatisfiabilityTestOutput> {

	public CliIncrementalClassExpressionSatisfiabilityQueryTest(
			final TestManifest<ClassQueryTestInput<ElkClassExpression>> manifest) {
		super(manifest,
				new CliIncrementalReasoningTestDelegate<SatisfiabilityTestOutput, SatisfiabilityTestOutput>(
						manifest) {

					@Override
					public SatisfiabilityTestOutput getExpectedOutput()
							throws Exception {
						final boolean isSatisfiable = standardReasoner_
								.isSatisfiableQuietly(
										manifest.getInput().getClassQuery());
						return new BaseSatisfiabilityTestOutput(isSatisfiable);
					}

					@Override
					public SatisfiabilityTestOutput getActualOutput()
							throws Exception {
						final boolean isSatisfiable = incrementalReasoner_
								.isSatisfiableQuietly(
										manifest.getInput().getClassQuery());
						return new BaseSatisfiabilityTestOutput(isSatisfiable);
					}

				});
	}

}
