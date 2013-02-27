package org.semanticweb.elk.reasoner.saturation.properties;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkAxiomIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;

public class CompositionClosureTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	/**
	 * testing if the flag {@link
	 * SaturatedPropertyChain.#REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES} is
	 * working for role composition computation.
	 * 
	 * @throws ElkException
	 * @throws InterruptedException
	 */
	@Test
	public void testReplaceByToldSuperPropertiesAndEliminateImplied()
			throws ElkException, InterruptedException {
		if (!SaturatedPropertyChain.REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES)
			return;

		ModifiableOntologyIndex index = new DirectIndex();
		ElkAxiomIndexerVisitor indexer = new ElkAxiomIndexerVisitor(index, true);

		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				":r"));
		ElkObjectProperty s = objectFactory.getObjectProperty(new ElkFullIri(
				":s"));
		ElkObjectProperty h1 = objectFactory.getObjectProperty(new ElkFullIri(
				":h1"));
		ElkObjectProperty h2 = objectFactory.getObjectProperty(new ElkFullIri(
				":h2"));
		ElkObjectProperty h3 = objectFactory.getObjectProperty(new ElkFullIri(
				":h3"));
		ElkObjectProperty h4 = objectFactory.getObjectProperty(new ElkFullIri(
				":h4"));
		ElkObjectProperty h5 = objectFactory.getObjectProperty(new ElkFullIri(
				":h5"));
		ElkObjectPropertyChain ros = objectFactory
				.getObjectPropertyChain(Arrays.asList(r, s));

		objectFactory.getSubObjectPropertyOfAxiom(ros, h1).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(ros, h2).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(ros, h3).accept(indexer);

		objectFactory.getSubObjectPropertyOfAxiom(h3, h4).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(h4, h2).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(h2, h5).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(h5, h1).accept(indexer);

		IndexedPropertyChain ih1 = index.getIndexed(h1);
		IndexedPropertyChain ih2 = index.getIndexed(h2);
		IndexedPropertyChain ih3 = index.getIndexed(h3);
		IndexedBinaryPropertyChain iros = (IndexedBinaryPropertyChain) index
				.getIndexed(ros);

		CompositionClosure closure = new CompositionClosure(iros);

		Set<IndexedPropertyChain> compositions = new HashSet<IndexedPropertyChain>(
				5);

		closure.applyTo(compositions);

		// testing that compositions = [h1, h2, h3]

		// System.err.println(compositions);

		assertTrue(compositions.contains(ih1));
		assertTrue(compositions.contains(ih2));
		assertTrue(compositions.contains(ih3));
		assertEquals(3, compositions.size());

		compositions.clear();

		// use the same example with elimination of implied properties
		CompositionClosure reducingClosure = new ReducingCompositionClosure(
				iros);

		reducingClosure.applyTo(compositions);

		// testing that compositions = [h3] because h3 => h2 => h1

		// System.err.println(compositions);

		assertTrue(compositions.contains(ih3));
		assertEquals(1, compositions.size());

	}

	@Test
	public void testEquivalentProperties() throws ElkException,
			InterruptedException {
		if (!SaturatedPropertyChain.REPLACE_CHAINS_BY_TOLD_SUPER_PROPERTIES)
			return;

		ModifiableOntologyIndex index = new DirectIndex();
		ElkAxiomIndexerVisitor indexer = new ElkAxiomIndexerVisitor(index, true);

		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				":r"));
		ElkObjectProperty s = objectFactory.getObjectProperty(new ElkFullIri(
				":s"));
		ElkObjectProperty h1 = objectFactory.getObjectProperty(new ElkFullIri(
				":h1"));
		ElkObjectProperty h2 = objectFactory.getObjectProperty(new ElkFullIri(
				":h2"));
		ElkObjectProperty h3 = objectFactory.getObjectProperty(new ElkFullIri(
				":h3"));
		ElkObjectProperty h4 = objectFactory.getObjectProperty(new ElkFullIri(
				":h4"));
		ElkObjectProperty h5 = objectFactory.getObjectProperty(new ElkFullIri(
				":h5"));
		ElkObjectPropertyChain ros = objectFactory
				.getObjectPropertyChain(Arrays.asList(r, s));

		objectFactory.getSubObjectPropertyOfAxiom(ros, h1).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(ros, h2).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(ros, h3).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(ros, h4).accept(indexer);

		objectFactory.getSubObjectPropertyOfAxiom(h3, h4).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(h2, h5).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(h5, h3).accept(indexer);
		objectFactory.getSubObjectPropertyOfAxiom(h3, h2).accept(indexer);

		// so we have h2 = h3 => h4

		IndexedPropertyChain ih1 = index.getIndexed(h1);
		IndexedPropertyChain ih2 = index.getIndexed(h2);
		IndexedPropertyChain ih3 = index.getIndexed(h3);
		IndexedBinaryPropertyChain iros = (IndexedBinaryPropertyChain) index
				.getIndexed(ros);

		CompositionClosure closure = new ReducingCompositionClosure(iros);

		Set<IndexedPropertyChain> compositions = new HashSet<IndexedPropertyChain>(
				5);

		closure.applyTo(compositions);

		// testing that compositions = [h1, h2] or [h1, h3]

		// System.err.println(compositions);

		assertTrue(compositions.contains(ih1));
		assertEquals(2, compositions.size());
		assertTrue(compositions.contains(ih2) || compositions.contains(ih3));

	}
}
