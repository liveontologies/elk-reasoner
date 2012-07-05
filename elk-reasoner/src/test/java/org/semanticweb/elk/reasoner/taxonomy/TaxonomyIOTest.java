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
/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.stages.TestStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;

/**
 * Tests loading/dumping of taxonomies
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class TaxonomyIOTest {

	private final Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();

	@Test
	public void classTaxonomyRoundtrip() throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException, ElkException {
		Taxonomy<ElkClass> original = loadAndClassify("io/taxonomy.owl");
		StringWriter writer = new StringWriter();

		TaxonomyPrinter.dumpClassTaxomomy(original, writer, false);

		StringReader reader = new StringReader(writer.getBuffer().toString());
		Owl2Parser parser = parserFactory.getParser(reader);
		Taxonomy<ElkClass> loaded = TaxonomyLoader.load(parser);

		// compare
		assertEquals(TaxonomyHasher.hash(original), TaxonomyHasher.hash(loaded));
	}

	@Test
	public void instanceTaxonomyRoundtrip() throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException, ElkException {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> original = loadAndClassify("io/instance_taxonomy.owl");
		StringWriter writer = new StringWriter();

		/*
		 * Writer outWriter = new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpInstanceTaxomomy(original, outWriter, false);
		 * outWriter.flush();
		 */

		TaxonomyPrinter.dumpInstanceTaxomomy(original, writer, false);

		StringReader reader = new StringReader(writer.getBuffer().toString());
		Owl2Parser parser = parserFactory.getParser(reader);
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> loaded = TaxonomyLoader
				.load(parser);

		/*
		 * System.out.println("================================="); outWriter =
		 * new OutputStreamWriter(System.out);
		 * TaxonomyPrinter.dumpInstanceTaxomomy(loaded, outWriter, false);
		 * outWriter.flush();
		 */

		// compare
		assertEquals(InstanceTaxonomyHasher.hash(original),
				InstanceTaxonomyHasher.hash(loaded));
	}

	/*
	 * Test that reordering classes in EquivalentClasses axioms and replacing a
	 * class name by an equivalent one in SubClassOf axioms does not break class
	 * taxonomy equivalence.
	 */
	@Test
	public void taxonomyEquivalence() throws IOException, Owl2ParseException,
			ElkInconsistentOntologyException {
		Taxonomy<ElkClass> taxonomy1 = load("io/taxonomy_eq_1.owl");
		Taxonomy<ElkClass> taxonomy2 = load("io/taxonomy_eq_2.owl");

		assertEquals(TaxonomyPrinter.getHashString(taxonomy1),
				TaxonomyPrinter.getHashString(taxonomy2));
	}

	@Test
	public void loadInconsistent() throws IOException, Owl2ParseException,
			ElkInconsistentOntologyException {
		Taxonomy<ElkClass> taxonomy = load("io/inconsistent.owl");

		assertEquals(1, taxonomy.getNodes().size());
		assertSame(taxonomy.getTopNode(), taxonomy.getBottomNode());
	}

	private InstanceTaxonomy<ElkClass, ElkNamedIndividual> loadAndClassify(
			String resource) throws IOException, Owl2ParseException,
			ElkInconsistentOntologyException, ElkException {

		InputStream stream = null;

		try {
			stream = getClass().getClassLoader().getResourceAsStream(resource);
			TestReasoner reasoner = new TestReasoner(stream,
					new TestStageExecutor());

			return reasoner.getInstanceTaxonomy();
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private Taxonomy<ElkClass> load(String resource) throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException {
		InputStream stream = null;

		try {
			stream = getClass().getClassLoader().getResourceAsStream(resource);

			return TaxonomyLoader.load(parserFactory.getParser(stream));
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
}

class TestReasoner extends Reasoner {

	protected TestReasoner(InputStream stream,
			ReasonerStageExecutor stageExecutor) {
		super(stageExecutor, Executors.newSingleThreadExecutor(), 1);
		registerOntologyLoader(new Owl2StreamLoader(
				new Owl2FunctionalStyleParserFactory(), stream));
		registerOntologyChangesLoader(new EmptyChangesLoader());
	}
}