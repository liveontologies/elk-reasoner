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
package org.semanticweb.elk.reasoner;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.taxonomy.inconsistent.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.TestResultComparisonException;

/**
 * Runs classification tests for all test input in the test directory
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @param <EO> 
 * 
 */
@RunWith(PolySuite.class)
public abstract class BaseClassificationCorrectnessTest<EO extends TestOutput>
		extends BaseReasoningCorrectnessTest<EO, ClassTaxonomyTestOutput> {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	public BaseClassificationCorrectnessTest(
			ReasoningTestManifest<EO, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	/*
	 * Tests
	 */

	/**
	 * Checks that the computed taxonomy is correct and complete
	 * 
	 * @throws TestResultComparisonException
	 *             in case the comparison fails
	 * @throws ElkException
	 */
	@Test
	public void classify() throws TestResultComparisonException, ElkException {
		Taxonomy<ElkClass> taxonomy;

		try {
			taxonomy = reasoner.getTaxonomy();
			manifest.compare(new ClassTaxonomyTestOutput(taxonomy));
		} catch (ElkInconsistentOntologyException e) {
			manifest.compare(new ClassTaxonomyTestOutput(
					PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY));
		}
	}

	/**
	 * Compute the taxonomy using interruptions and checks that the computed
	 * taxonomy is correct and complete
	 * 
	 * @throws TestResultComparisonException
	 *             in case the comparison fails
	 * @throws ElkException
	 */
	@Test
	public void classifyWithInterruptions()
			throws TestResultComparisonException, ElkException {
		ReasoningProcess reasoningProcess = new ReasoningProcess();
		Thread reasonerThread = new Thread(reasoningProcess,
				"test-elk-reasoner-thread");
		reasonerThread.start();

		while (reasonerThread.isAlive()) {
			// interrupt every millisecond
			reasoner.interrupt();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				fail();
			}
		}

		if (reasoningProcess.exception != null)
			throw reasoningProcess.exception;
		if (reasoningProcess.consistent) {
			manifest.compare(new ClassTaxonomyTestOutput(reasoningProcess
					.getTaxonomy()));
		} else {
			manifest.compare(new ClassTaxonomyTestOutput(
					PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY));
		}

	}

	/**
	 * A simple class for running a reasoner in a separate thread and query the
	 * result
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class ReasoningProcess implements Runnable {

		Taxonomy<ElkClass> taxonomy = null;
		boolean consistent = true;
		ElkException exception = null;

		@Override
		public void run() {
			try {
				taxonomy = reasoner.getTaxonomy();
			} catch (ElkInconsistentOntologyException e) {
				consistent = false;
			} catch (ElkException e) {
				exception = e;
			}
		}

		public Taxonomy<ElkClass> getTaxonomy() {
			return this.taxonomy;
		}

		public boolean isConsistent() {
			return consistent;
		}

	};

}