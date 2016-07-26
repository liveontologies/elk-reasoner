/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.query;

import java.net.URL;

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.TestResultComparisonException;

/**
 * Test manifest for complex class query tests. Test input consists of an
 * ontology and a query class expression.
 * 
 * @author Peter Skocovsky
 *
 * @param <Ont>
 *            Type of the ontology.
 * @param <C>
 *            Type of the query class.
 * @param <O>
 *            Type of the test output.
 */
public abstract class BaseClassExpressionQueryTestManifest<Ont, C, O extends TestOutput>
		implements TestManifest<ClassQueryTestInput<Ont, C>, O, O> {

	private final String testName_;
	private final String inputName_;
	private final Ont inputOntology_;
	private final C queryClass_;

	public BaseClassExpressionQueryTestManifest(final String testName,
			final String inputName, final Ont inputOntology,
			final C queryClass) {
		this.testName_ = testName;
		this.inputName_ = inputName;
		this.inputOntology_ = inputOntology;
		this.queryClass_ = queryClass;
	}

	/**
	 * Constructor that extract test name and input name from the input URL.
	 * 
	 * @param input
	 * @param inputOntology
	 * @param queryClass
	 */
	public BaseClassExpressionQueryTestManifest(final URL input,
			final Ont inputOntology, final C queryClass) {
		this(FileUtils.getFileName(FileUtils.dropExtension(input.getPath())),
				FileUtils.getFileName(input.getPath()), inputOntology,
				queryClass);
	}

	@Override
	public String getName() {
		return testName_;
	}

	@Override
	public ClassQueryTestInput<Ont, C> getInput() {
		return new ClassQueryTestInput<Ont, C>() {

			@Override
			public String getName() {
				return inputName_;
			}

			@Override
			public Ont getOntology() {
				return inputOntology_;
			}

			@Override
			public C getClassQuery() {
				return queryClass_;
			}

		};
	}

	@Override
	public void compare(final O actualOutput)
			throws TestResultComparisonException {
		final O expectedOutput = getExpectedOutput();
		final String message = "Expected output is not equal to the actual output\n"
				+ "expected:\n" + expectedOutput + "\n" + "actual:\n"
				+ actualOutput + "\n";
		if (expectedOutput == null ? actualOutput != null
				: !expectedOutput.equals(actualOutput)) {
			throw new TestResultComparisonException(message, expectedOutput,
					actualOutput);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + testName_ + ")";
	}

}
