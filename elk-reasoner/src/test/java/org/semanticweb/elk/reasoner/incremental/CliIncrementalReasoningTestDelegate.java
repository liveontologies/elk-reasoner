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
package org.semanticweb.elk.reasoner.incremental;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

public abstract class CliIncrementalReasoningTestDelegate<EO extends TestOutput, AO extends TestOutput>
		implements IncrementalReasoningTestDelegate<ElkAxiom, EO, AO> {

	protected final TestManifest<? extends UrlTestInput> manifest_;

	protected Collection<ElkAxiom> allAxioms_;

	protected Reasoner standardReasoner_;
	protected Reasoner incrementalReasoner_;

	public CliIncrementalReasoningTestDelegate(
			TestManifest<? extends UrlTestInput> manifest) {
		this.manifest_ = manifest;
	}

	@Override
	public Collection<ElkAxiom> loadAxioms() throws Exception {

		this.allAxioms_ = new ArrayList<ElkAxiom>();
		final Collection<ElkAxiom> changingAxioms = new ArrayList<ElkAxiom>();

		InputStream stream = null;

		try {
			stream = manifest_.getInput().getUrl().openStream();

			final Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
					.getParser(stream);
			parser.accept(new Owl2ParserAxiomProcessor() {

				@Override
				public void visit(ElkPrefix elkPrefix)
						throws Owl2ParseException {
					// does nothing
				}

				@Override
				public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
					// all axioms are dynamic
					changingAxioms.add(elkAxiom);
					allAxioms_.add(elkAxiom);
				}

				@Override
				public void finish() throws Owl2ParseException {
					// everything is processed immediately
				}

			});

			return changingAxioms;

		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Override
	public void init() throws Exception {
		standardReasoner_ = TestReasonerUtils.createTestReasoner(
				new TestChangesLoader(allAxioms_, IncrementalChangeType.ADD),
				new PostProcessingStageExecutor());
		standardReasoner_.setAllowIncrementalMode(false);
		incrementalReasoner_ = TestReasonerUtils.createTestReasoner(
				new TestChangesLoader(allAxioms_, IncrementalChangeType.ADD),
				new PostProcessingStageExecutor());
		incrementalReasoner_.setAllowIncrementalMode(true);
	}

	@Override
	public void applyChanges(final Iterable<ElkAxiom> changes,
			final IncrementalChangeType type) {
		standardReasoner_
				.registerAxiomLoader(new TestChangesLoader(changes, type));
		incrementalReasoner_
				.registerAxiomLoader(new TestChangesLoader(changes, type));
	}

	@Override
	public void dumpChangeToLog(final ElkAxiom change, final Logger logger,
			final LogLevel level) {
		LoggerWrap.log(logger, level,
				OwlFunctionalStylePrinter.toString(change) + ": deleted");
	}

	@Override
	public void dispose() {
		// Empty.
	}

}
