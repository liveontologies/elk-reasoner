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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.runner.RunWith;
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public class IncrementalClassificationCorrectnessTest
		extends
		BaseIncrementalReasoningCorrectnessTest<ElkAxiom, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	public IncrementalClassificationCorrectnessTest(
			ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> testManifest) {
		super(testManifest);
	}

	@Override
	protected void correctnessCheck(Reasoner standardReasoner,
			Reasoner incrementalReasoner, long seed) throws ElkException {
		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("======= Computing Expected Taxonomy =======");

		Taxonomy<ElkClass> expected = standardReasoner.getTaxonomyQuietly();

		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("======= Computing Incremental Taxonomy =======");

		Taxonomy<ElkClass> incremental;
		try {
			incremental = incrementalReasoner.getTaxonomyQuietly();
		} catch (Exception e) {
			throw new RuntimeException("Seed: " + seed, e);
		}

		try {
			assertEquals("Seed " + seed, TaxonomyHasher.hash(expected),
					TaxonomyHasher.hash(incremental));
		} catch (AssertionError e) {
			try {
				Writer writer = new OutputStreamWriter(System.out);
				System.out.println("======= Expected Taxonomy =======");
				TaxonomyPrinter.dumpClassTaxomomy(expected, writer, false);
				System.out.println("======= Incremental Taxonomy =======");
				TaxonomyPrinter.dumpClassTaxomomy(incremental, writer, false);
				writer.flush();
				throw e;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	
	@Override
	protected void applyChanges(
			final Reasoner reasoner,
			final Iterable<ElkAxiom> changes,
			final BaseIncrementalReasoningCorrectnessTest.CHANGE type) {

		reasoner.registerOntologyChangesLoader(new ChangesLoader() {
			
			@Override
			public void registerChangeListener(AxiomChangeListener listener) {
			}
			
			@Override
			public Loader getLoader(final ElkAxiomProcessor axiomInserter,
					final ElkAxiomProcessor axiomDeleter) {
				return new Loader() {

					@Override
					public void load() throws ElkLoadingException {
						for (ElkAxiom axiom : changes) {
							switch (type) {
							case ADD:
								axiomInserter.visit(axiom);
								break;
							case DELETE:
								axiomDeleter.visit(axiom);
								break;
							}
						}
					}

					@Override
					public void dispose() {
					}
				};
			}
		});
	}

	@Override
	protected void dumpChangeToLog(ElkAxiom change) {
		LOGGER_.trace(OwlFunctionalStylePrinter.toString(change) + ": deleted");
	}

	@Override
	protected void loadAxioms(InputStream stream, final List<ElkAxiom> staticAxioms,
			final OnOffVector<ElkAxiom> changingAxioms) throws IOException,
			Owl2ParseException {
		
		Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(stream);
		parser.accept(new Owl2ParserAxiomProcessor() {

			@Override
			public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
			}

			@Override
			public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				if (elkAxiom instanceof ElkPropertyAxiom<?>) {
					staticAxioms.add(elkAxiom);
				}
				else {
					changingAxioms.add(elkAxiom);
				}
			}
		});
	}

	@Override
	protected Reasoner getReasoner(final Iterable<ElkAxiom> axioms) {
		Reasoner reasoner = TestReasonerUtils
				.createTestReasoner(new PostProcessingStageExecutor());
		reasoner.registerOntologyLoader(new TestAxiomLoader(axioms));

		return reasoner;
	}	
	
	
	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						IncrementalClassificationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<URLTestIO, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>() {
							@Override
							public TestManifest<URLTestIO, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TaxonomyDiffManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>(
										input, null);
							}
						});
	}


}
