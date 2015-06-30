/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public class IncrementalClassificationCorrectnessTest extends
		BaseIncrementalClassificationCorrectnessTest<ElkAxiom> {

	public IncrementalClassificationCorrectnessTest(
			ReasoningTestManifest<ClassTaxonomyTestOutput<?>, ClassTaxonomyTestOutput<?>> testManifest) {
		super(testManifest);
	}

	@Override
	protected void applyChanges(final Reasoner reasoner,
			final Iterable<ElkAxiom> changes, final IncrementalChangeType type) {
		reasoner.registerAxiomLoader(new TestChangesLoader(changes, type));
	}

	@Override
	protected void dumpChangeToLog(ElkAxiom change, LogLevel level) {
		LoggerWrap.log(LOGGER_, level,
				OwlFunctionalStylePrinter.toString(change) + ": deleted");
	}

	@Override
	protected void loadAxioms(InputStream stream,
			final List<ElkAxiom> inputStaticAxioms,
			final OnOffVector<ElkAxiom> inputChangingAxioms)
			throws IOException, Owl2ParseException {

		Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(stream);
		parser.accept(new Owl2ParserAxiomProcessor() {

			@Override
			public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
				// does nothing
			}

			@Override
			public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				// all axioms are dynamic
				changingAxioms.add(elkAxiom);
			}

			@Override
			public void finish() throws Owl2ParseException {
				// everything is processed immediately
			}
		});
	}

	@Override
	protected Reasoner getReasoner(final Iterable<ElkAxiom> axioms) {
		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new TestChangesLoader(axioms, IncrementalChangeType.ADD),
				new PostProcessingStageExecutor());

		return reasoner;
	}

}
