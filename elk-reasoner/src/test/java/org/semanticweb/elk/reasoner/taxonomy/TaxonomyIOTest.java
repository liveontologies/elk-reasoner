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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * Tests loading/dumping of taxonomies.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class TaxonomyIOTest {

	/**
	 * The {@link ElkObject.Factory} used for construction of {@link ElkObject}
	 * s. In order for {@link MockTaxonomyLoader} to work correctly, the factory
	 * should identify all {@link ElkEntity} objects.
	 */
	private final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

	/**
	 * The {@link Owl2ParserFactory} used for parsing the input file. It should
	 * use the same {@link ElkObject.Factory} as in all other places to avoid
	 * creation of different objects for the same {@link ElkIri}s.
	 */
	private final Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory(
			objectFactory);

	@Test
	public void classTaxonomyRoundtrip() throws IOException, Owl2ParseException,
			ElkInconsistentOntologyException, ElkException {
		Taxonomy<ElkClass> original = loadAndClassify("io/taxonomy.owl");
		StringWriter writer = new StringWriter();

		TaxonomyPrinter.dumpTaxomomy(original, writer, false);

		StringReader reader = new StringReader(writer.getBuffer().toString());
		Owl2Parser parser = parserFactory.getParser(reader);
		Taxonomy<ElkClass> loaded = MockTaxonomyLoader.load(objectFactory,
				parser);

		final StringWriter loadedWriter = new StringWriter();
		TaxonomyPrinter.dumpTaxomomy(loaded, loadedWriter, false);

		// compare
		// @formatter:off
		assertTrue("Taxonomies are not equal!\n"
				+ "original:\n"
				+ writer.getBuffer().toString()
				+ "\n"
				+ "loaded:\n"
				+ loadedWriter.getBuffer().toString(),
				TaxonomyHasher.hash(original) == TaxonomyHasher.hash(loaded)
				&& original.equals(loaded));
		// @formatter:on
	}

	@Test
	public void instanceTaxonomyRoundtrip() throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException, ElkException {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> original = loadAndClassify(
				"io/instance_taxonomy.owl");
		StringWriter writer = new StringWriter();

		TaxonomyPrinter.dumpInstanceTaxomomy(original, writer, false);

		StringReader reader = new StringReader(writer.getBuffer().toString());
		Owl2Parser parser = parserFactory.getParser(reader);
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> loaded = MockTaxonomyLoader
				.load(objectFactory, parser);

		final StringWriter loadedWriter = new StringWriter();
		TaxonomyPrinter.dumpInstanceTaxomomy(loaded, loadedWriter, false);

		// compare
		// @formatter:off
		assertTrue("Taxonomies are not equal!\n"
				+ "original:\n"
				+ writer.getBuffer().toString()
				+ "\n"
				+ "loaded:\n"
				+ loadedWriter.getBuffer().toString(),
				InstanceTaxonomyHasher.hash(original) == InstanceTaxonomyHasher.hash(loaded)
				&& original.equals(loaded));
		// @formatter:on
	}

	@Test
	public void objectPropertyTaxonomyRoundtrip() throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException, ElkException {
		final Taxonomy<ElkObjectProperty> original = loadAndClassifyObjectProperties(
				"io/property_taxonomy.owl");
		final StringWriter writer = new StringWriter();

		TaxonomyPrinter.dumpTaxomomy(original, writer, false);

		final StringReader reader = new StringReader(
				writer.getBuffer().toString());
		final Owl2Parser parser = parserFactory.getParser(reader);
		final Taxonomy<ElkObjectProperty> loaded = MockObjectPropertyTaxonomyLoader
				.load(objectFactory, parser);

		final StringWriter loadedWriter = new StringWriter();
		TaxonomyPrinter.dumpTaxomomy(loaded, loadedWriter, false);

		// compare
		// @formatter:off
		assertTrue("Taxonomies are not equal!\n"
				+ "original:\n"
				+ writer.getBuffer().toString()
				+ "\n"
				+ "loaded:\n"
				+ loadedWriter.getBuffer().toString(),
				TaxonomyHasher.hash(original) == TaxonomyHasher.hash(loaded)
				&& original.equals(loaded));
		// @formatter:on
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

	/*
	 * Test that reordering properties in EquivalentObjectProperties axioms and
	 * replacing a property name by an equivalent one in SubObjectPropertyOf
	 * axioms does not break property taxonomy equivalence.
	 */
	@Test
	public void ovjectPropertyTaxonomyEquivalence() throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException {
		final Taxonomy<ElkObjectProperty> taxonomy1 = loadObjectPropertyTaxonomy(
				"io/property_taxonomy_eq_1.owl");
		final Taxonomy<ElkObjectProperty> taxonomy2 = loadObjectPropertyTaxonomy(
				"io/property_taxonomy_eq_2.owl");

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

	@Test
	public void loadInconsistentObjectPropertyTaxonomy() throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException {
		final Taxonomy<ElkObjectProperty> taxonomy = loadObjectPropertyTaxonomy(
				"io/property_inconsistent.owl");

		assertEquals(1, taxonomy.getNodes().size());
		assertSame(taxonomy.getTopNode(), taxonomy.getBottomNode());
	}

	private InstanceTaxonomy<ElkClass,ElkNamedIndividual> loadAndClassify(String resource) throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException, ElkException {

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				getClass().getClassLoader().getResourceAsStream(resource),
				new SimpleStageExecutor(), 1);

		return reasoner.getInstanceTaxonomy();
	}

	private Taxonomy<ElkObjectProperty> loadAndClassifyObjectProperties(String resource) throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException, ElkException {

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				getClass().getClassLoader().getResourceAsStream(resource),
				new SimpleStageExecutor(), 1);

		return reasoner.getObjectPropertyTaxonomy();
	}

	private Taxonomy<ElkClass> load(String resource) throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException {
		InputStream stream = null;

		try {
			stream = getClass().getClassLoader().getResourceAsStream(resource);
			return MockTaxonomyLoader.load(objectFactory,
					parserFactory.getParser(stream));
		} finally {
			if (stream != null) {
				IOUtils.closeQuietly(stream);
			}
		}
	}

	private Taxonomy<ElkObjectProperty> loadObjectPropertyTaxonomy(
			final String resource) throws IOException, Owl2ParseException,
					ElkInconsistentOntologyException {
		InputStream stream = null;

		try {
			stream = getClass().getClassLoader().getResourceAsStream(resource);
			return MockObjectPropertyTaxonomyLoader.load(objectFactory,
					parserFactory.getParser(stream));
		} finally {
			if (stream != null) {
				IOUtils.closeQuietly(stream);
			}
		}
	}

}