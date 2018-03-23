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

import static org.junit.Assume.assumeTrue;

import java.net.URL;

import org.semanticweb.elk.testing.BasicTestManifest;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 * 
 * @param <O>
 *            the type of the reasoning output
 */
public class ReasoningTestManifest<O extends ReasoningTestOutput<?>>
		extends BasicTestManifest<UrlTestInput, O> {

	public ReasoningTestManifest(final String name, URL input, O expOutput) {
		super(new URLTestIO(name, input), expOutput);
	}

	@Override
	public void compare(final O actualOutput) {
		assumeTrue(getExpectedOutput().isComplete());
		if (actualOutput.isComplete()) {
			super.compare(actualOutput);
		}
		// TODO: check inclusion of incomplete results
	}

}
