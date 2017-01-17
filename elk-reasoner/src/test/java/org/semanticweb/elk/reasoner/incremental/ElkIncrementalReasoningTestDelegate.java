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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.RandomReasonerInterrupter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.ElkInterruptedException;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

public abstract class ElkIncrementalReasoningTestDelegate<EO extends TestOutput, AO extends TestOutput>
		implements
		IncrementalReasoningTestWithInterruptsDelegate<ElkAxiom, EO, AO> {

	public static final double DEFAULT_INTERRUPTION_CHANCE = 0.1;

	private final TestManifest<? extends UrlTestInput> manifest_;

	private final double interruptionChance_;

	private final Collection<ElkAxiom> allAxioms_ = new ArrayList<ElkAxiom>();
	private Reasoner standardReasoner_;
	private Reasoner incrementalReasoner_;

	public ElkIncrementalReasoningTestDelegate(
			TestManifest<? extends UrlTestInput> manifest,
			final double interruptionChance) {
		this.manifest_ = manifest;
		this.interruptionChance_ = interruptionChance;
	}

	public ElkIncrementalReasoningTestDelegate(
			TestManifest<? extends UrlTestInput> manifest) {
		this(manifest, DEFAULT_INTERRUPTION_CHANCE);
	}

	public TestManifest<? extends UrlTestInput> getManifest() {
		return manifest_;
	}

	public Reasoner getStandardReasoner() {
		return standardReasoner_;
	}

	public Reasoner getIncrementalReasoner() {
		return incrementalReasoner_;
	}

	@Override
	public Collection<ElkAxiom> load() throws Exception {

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
	public void initIncremental() throws Exception {

		standardReasoner_ = TestReasonerUtils.createTestReasoner(
				new TestChangesLoader(allAxioms_, IncrementalChangeType.ADD));
		standardReasoner_.setAllowIncrementalMode(false);
		incrementalReasoner_ = TestReasonerUtils.createTestReasoner(
				new TestChangesLoader(allAxioms_, IncrementalChangeType.ADD));
		incrementalReasoner_.setAllowIncrementalMode(true);

	}

	@Override
	public double getInterruptionChance() {
		return interruptionChance_;
	}

	@Override
	public void initWithInterrupts() throws Exception {

		standardReasoner_ = TestReasonerUtils.createTestReasoner(
				new TestChangesLoader(allAxioms_, IncrementalChangeType.ADD));
		standardReasoner_.setAllowIncrementalMode(false);
		final Random random = new Random(RandomSeedProvider.VALUE);
		incrementalReasoner_ = TestReasonerUtils.createTestReasoner(
				manifest_.getInput().getUrl().openStream(),
				new RandomReasonerInterrupter(random, getInterruptionChance()));
		incrementalReasoner_.setAllowIncrementalMode(true);

	}

	@Override
	public void applyChanges(final Iterable<ElkAxiom> changes,
			final IncrementalChangeType type) {
		standardReasoner_.registerAxiomLoader(new TestAxiomLoaderFactory(
				new TestChangesLoader(changes, type)));
		incrementalReasoner_.registerAxiomLoader(new TestAxiomLoaderFactory(
				new TestChangesLoader(changes, type)));
	}

	@Override
	public void dumpChangeToLog(final ElkAxiom change, final Logger logger,
			final LogLevel level) {
		LoggerWrap.log(logger, level,
				OwlFunctionalStylePrinter.toString(change) + ": deleted");
	}

	@Override
	public Class<? extends Exception> getInterruptionExceptionClass() {
		return ElkInterruptedException.class;
	}

	@Override
	public void before() throws Exception {
		// Empty.
	}

	@Override
	public void after() {
		try {
			assertTrue(standardReasoner_.shutdown());
			assertTrue(incrementalReasoner_.shutdown());
		} catch (InterruptedException e) {
			fail();
		}
	}

}
