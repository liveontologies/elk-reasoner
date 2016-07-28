/*
 * #%L
 * ELK Command Line Interface
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
package org.semanticweb.elk.cli;

import java.io.InputStream;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.ReasoningTestWithInterruptsDelegate;
import org.semanticweb.elk.reasoner.stages.RestartingStageExecutor;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;

public abstract class CliReasoningTestDelegate<AO extends TestOutput>
		implements ReasoningTestWithInterruptsDelegate<AO> {

	protected final TestManifest<? extends UrlTestInput> manifest_;

	private InputStream input_ = null;
	protected Reasoner reasoner_;

	public CliReasoningTestDelegate(
			final TestManifest<? extends UrlTestInput> manifest) {
		this.manifest_ = manifest;
	}

	@Override
	public void init() throws Exception {
		input_ = manifest_.getInput().getUrl().openStream();
		final AxiomLoader loader = new Owl2StreamLoader(
				new Owl2FunctionalStyleParserFactory(), input_);
		reasoner_ = new ReasonerFactory().createReasoner(loader,
				new RestartingStageExecutor());
	}

	@Override
	public void interrupt() {
		reasoner_.interrupt();
	}

	@Override
	public void dispose() {
		IOUtils.closeQuietly(input_);
	}

}
