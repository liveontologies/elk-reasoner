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
package org.semanticweb.elk.owlapi;

import java.io.InputStream;

import org.semanticweb.elk.reasoner.ReasoningTestWithInterruptsDelegate;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;

public abstract class OwlApiReasoningTestDelegate<AO extends TestOutput>
		implements ReasoningTestWithInterruptsDelegate<AO> {

	protected final TestManifest<? extends UrlTestInput> manifest_;

	protected ElkReasoner reasoner_;

	public OwlApiReasoningTestDelegate(
			final TestManifest<? extends UrlTestInput> manifest) {
		this.manifest_ = manifest;
	}

	@Override
	public void init() throws Exception {
		final InputStream input = manifest_.getInput().getUrl().openStream();
		reasoner_ = OWLAPITestUtils.createReasoner(input);
	}

	@Override
	public void interrupt() {
		reasoner_.getInternalReasoner().interrupt();
	}

	@Override
	public void dispose() {
		// Empty.
	}

}
