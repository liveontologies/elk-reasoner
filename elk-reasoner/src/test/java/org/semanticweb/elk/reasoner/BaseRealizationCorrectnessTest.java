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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.inconsistent.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.TestResultComparisonException;

/**
 * Runs ABox realization tests for all test input in the test directory
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @param <EO> 
 */
@RunWith(PolySuite.class)
public abstract class BaseRealizationCorrectnessTest<EO extends TestOutput>
		extends BaseReasoningCorrectnessTest<EO, InstanceTaxonomyTestOutput> {

	final static String INPUT_DATA_LOCATION = "realization_test_input";

	public BaseRealizationCorrectnessTest(
			ReasoningTestManifest<EO, InstanceTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	/*
	 * Tests
	 */
	/**
	 * Checks that the computed taxonomy with instances is correct and complete
	 * 
	 * @throws TestResultComparisonException
	 *             in case the comparison fails
	 * @throws ElkException
	 */
	@Test
	public void realize() throws TestResultComparisonException, ElkException {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy;
		try {
			taxonomy = reasoner.getInstanceTaxonomy();
			manifest.compare(new InstanceTaxonomyTestOutput(taxonomy));
		} catch (ElkInconsistentOntologyException e) {
			manifest.compare(new InstanceTaxonomyTestOutput(
					PredefinedTaxonomy.INCONSISTENT_INDIVIDUAL_TAXONOMY));
		}
	}
}