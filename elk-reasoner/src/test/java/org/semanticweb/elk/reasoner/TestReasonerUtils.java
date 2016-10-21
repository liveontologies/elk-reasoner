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
package org.semanticweb.elk.reasoner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.loading.TestAxiomLoader;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class TestReasonerUtils {

	public static Reasoner createTestReasoner(
			final AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerInterrupter interrupter,
			final ReasonerStageExecutor stageExecutor,
			final ReasonerConfiguration config) {
		return new ReasonerFactory().createReasoner(axiomLoaderFactory,
				interrupter, stageExecutor, config);
	}

	/**
	 * Created a reasoner that fails on interrupt.
	 * 
	 * @param axiomLoaderFactory
	 * @param stageExecutor
	 * @param config
	 * @return
	 */
	public static Reasoner createTestReasoner(
			final AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerStageExecutor stageExecutor,
			final ReasonerConfiguration config) {
		return createTestReasoner(axiomLoaderFactory,
				FailingReasonerInterrupter.INSTANCE, stageExecutor, config);
	}

	/**
	 * Created a reasoner that fails on interrupt and uses default config.
	 * 
	 * @param axiomLoaderFactory
	 * @param stageExecutor
	 * @return
	 */
	public static Reasoner createTestReasoner(
			final AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerStageExecutor stageExecutor) {
		return createTestReasoner(axiomLoaderFactory, stageExecutor,
				ReasonerConfiguration.getConfiguration());
	}
	
	/**
	 * Created a reasoner that fails on interrupt and uses default config.
	 * 
	 * @param axiomLoader
	 * @param stageExecutor
	 * @return
	 */
	public static Reasoner createTestReasoner(final TestAxiomLoader axiomLoader,
			final ReasonerStageExecutor stageExecutor) {
		return createTestReasoner(new TestAxiomLoaderFactory(axiomLoader),
				stageExecutor);
	}
	
	/**
	 * Created a reasoner that fails on interrupt and uses default config.
	 * Closes the stream.
	 * 
	 * @param stream
	 * @param stageExecutor
	 * @return
	 */
	public static Reasoner createTestReasoner(final InputStream stream,
			final ReasonerStageExecutor stageExecutor) {
		final AxiomLoader.Factory axiomLoaderFactory = new Owl2StreamLoader.Factory(
				new Owl2FunctionalStyleParserFactory(), stream);
		return createTestReasoner(axiomLoaderFactory, stageExecutor);
	}

	/**
	 * Created a reasoner that fails on interrupt and uses specified number of
	 * workers.
	 * 
	 * @param axiomLoaderFactory
	 * @param stageExecutor
	 * @param maxWorkers
	 * @return
	 */
	public static Reasoner createTestReasoner(
			final AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerStageExecutor stageExecutor, final int maxWorkers) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				String.valueOf(maxWorkers));

		return createTestReasoner(axiomLoaderFactory, stageExecutor, config);
	}

	/**
	 * Created a reasoner that fails on interrupt and uses specified number of
	 * workers.
	 * 
	 * @param axiomLoader
	 * @param stageExecutor
	 * @param maxWorkers
	 * @return
	 */
	public static Reasoner createTestReasoner(final TestAxiomLoader axiomLoader,
			final ReasonerStageExecutor stageExecutor, final int maxWorkers) {
		return createTestReasoner(new TestAxiomLoaderFactory(axiomLoader),
				stageExecutor, maxWorkers);
	}

	/**
	 * Created a reasoner that fails on interrupt and uses specified number of
	 * workers. Closes the stream.
	 * 
	 * @param stream
	 * @param stageExecutor
	 * @param maxWorkers
	 * @return
	 */
	public static Reasoner createTestReasoner(final InputStream stream,
			final ReasonerStageExecutor stageExecutor, final int maxWorkers) {
		final AxiomLoader.Factory axiomLoaderFactory = new Owl2StreamLoader.Factory(
				new Owl2FunctionalStyleParserFactory(), stream);
		return createTestReasoner(axiomLoaderFactory, stageExecutor,
				maxWorkers);
	}

	/**
	 * Created a reasoner that uses default config.
	 * 
	 * @param stream
	 * @param interrupter
	 * @param stageExecutor
	 * @return
	 */
	public static Reasoner createTestReasoner(final InputStream stream,
			final ReasonerInterrupter interrupter,
			final ReasonerStageExecutor stageExecutor) {
		final AxiomLoader.Factory axiomLoaderFactory = new Owl2StreamLoader.Factory(
				new Owl2FunctionalStyleParserFactory(), stream);
		return createTestReasoner(axiomLoaderFactory, interrupter,
				stageExecutor, ReasonerConfiguration.getConfiguration());
	}

	public static Reasoner createTestReasoner(
			final ReasonerInterrupter interrupter,
			final ReasonerStageExecutor stageExecutor,
			final ReasonerConfiguration config) {
		return new ReasonerFactory().createReasoner(interrupter, stageExecutor,
				config);
	}

	public static Reasoner loadAndClassify(Set<? extends ElkAxiom> ontology)
			throws Exception {

		TestChangesLoader initialLoader = new TestChangesLoader();
		ReasonerStageExecutor executor = new LoggingStageExecutor();

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(initialLoader,
				executor);

		for (ElkAxiom axiom : ontology) {
			initialLoader.add(axiom);
		}

		try {
			reasoner.getTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			// shit happens
		}

		return reasoner;
	}

	public static Set<ElkAxiom> loadAxioms(InputStream stream)
			throws IOException, Owl2ParseException {
		return loadAxioms(new InputStreamReader(stream));
	}

	public static Set<? extends ElkAxiom> loadAxioms(String resource)
			throws Exception {
		return loadAxioms(TestReasonerUtils.class.getClassLoader()
				.getResourceAsStream(resource));
	}

	public static Set<? extends ElkAxiom> loadAxioms(File file)
			throws Exception {
		return loadAxioms(new FileInputStream(file));
	}

	public static Set<ElkAxiom> loadAxioms(Reader reader)
			throws IOException, Owl2ParseException {
		try {
			Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
					.getParser(reader);
			final Set<ElkAxiom> axioms = new HashSet<ElkAxiom>();
	
			parser.accept(new Owl2ParserAxiomProcessor() {
	
				@Override
				public void visit(ElkPrefix elkPrefix)
						throws Owl2ParseException {
					// ignored
				}
	
				@Override
				public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
					axioms.add(elkAxiom);
				}
	
				@Override
				public void finish() throws Owl2ParseException {
					// everything is processed immediately
				}
			});
	
			return axioms;
		} finally {
			reader.close();
		}
	}
}
