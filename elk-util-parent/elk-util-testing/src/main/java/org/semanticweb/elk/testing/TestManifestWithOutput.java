/*
 * #%L
 * ELK Utilities for Testing
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
package org.semanticweb.elk.testing;

/**
 * The base interface for a test instance which defines:
 * - the input
 * - the expected output
 * - the method of comparison
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 * @param <I> 
 * @param <EO> 
 * @param <AO> 
 *
 */
public interface TestManifestWithOutput<I extends TestInput, EO extends TestOutput, AO extends TestOutput>
		extends TestManifest<I> {

	public EO getExpectedOutput();
	public void compare(AO actualOutput) throws TestResultComparisonException;
}
