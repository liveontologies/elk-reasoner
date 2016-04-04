/**
 * 
 */
package org.semanticweb.elk.reasoner.taxonomy;

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
import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * Tests for our taxonomy validators
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TaxonomyValidatorTest {

	@Test
	public void testAllGood() throws Exception {
		Taxonomy<ElkClass> taxonomy = load("io/taxonomy.owl");
		TaxonomyValidator<ElkClass> validator = new BasicTaxonomyValidator<ElkClass>()
				.add(new TaxonomyNodeDisjointnessVisitor<ElkClass>(taxonomy))
				.add(new TaxonomyLinkConsistencyVisitor<ElkClass>());

		validator.validate(taxonomy);
		new TaxonomyAcyclicityAndReductionValidator<ElkClass>()
				.validate(taxonomy);
	}

	@Test(expected = InvalidTaxonomyException.class)
	public void testNodesNonDisjoint() throws Exception {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = createEmptyTaxonomy();
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		// doing this manually because the taxonomy will try to prevent us from
		// creating an invalid one
		taxonomy.getCreateTypeNode(Arrays.asList(A));
		taxonomy.getCreateTypeNode(Arrays.asList(C));
		// The nodes for A and C overlap on B
		taxonomy.getNode(A).members.add(B);
		taxonomy.getNode(C).members.add(B);

		TaxonomyValidator<ElkClass> validator = new BasicTaxonomyValidator<ElkClass>()
				.add(new TaxonomyNodeDisjointnessVisitor<ElkClass>(taxonomy));

		validator.validate(taxonomy);
	}

	@Test(expected = InvalidTaxonomyException.class)
	public void testNodeLinksInconsistent() throws Exception {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = createEmptyTaxonomy();
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		ElkClass D = factory.getClass(new ElkFullIri("#D"));
		// doing this manually because the taxonomy will try to prevent us from
		// creating an invalid one
		taxonomy.getCreateTypeNode(Arrays.asList(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B));
		taxonomy.getCreateTypeNode(Arrays.asList(C));
		taxonomy.getCreateTypeNode(Arrays.asList(D));

		taxonomy.getNode(C).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(B));
		taxonomy.parentMap.get(taxonomy.getNode(C)).add(
				taxonomy.getNode(B));

		TaxonomyValidator<ElkClass> validator = new BasicTaxonomyValidator<ElkClass>()
				.add(new TaxonomyLinkConsistencyVisitor<ElkClass>());

		validator.validate(taxonomy);
	}

	@Test
	public void testAcyclic() throws Exception {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = createEmptyTaxonomy();
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		ElkClass D = factory.getClass(new ElkFullIri("#D"));

		taxonomy.getCreateTypeNode(Arrays.asList(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B));
		taxonomy.getCreateTypeNode(Arrays.asList(C));
		taxonomy.getCreateTypeNode(Arrays.asList(D));

		taxonomy.getNode(B).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(C).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(B));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(C));

		new TaxonomyAcyclicityAndReductionValidator<ElkClass>()
				.validate(taxonomy);
	}

	@Test(expected = InvalidTaxonomyException.class)
	public void testCyclic() throws Exception {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = createEmptyTaxonomy();
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		ElkClass D = factory.getClass(new ElkFullIri("#D"));

		taxonomy.getCreateTypeNode(Arrays.asList(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B));
		taxonomy.getCreateTypeNode(Arrays.asList(C));
		taxonomy.getCreateTypeNode(Arrays.asList(D));

		taxonomy.getNode(B).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(C).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(B));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(C));
		// this one creates a cycle
		taxonomy.getNode(A).addDirectParent(taxonomy.getNode(D));

		new TaxonomyAcyclicityAndReductionValidator<ElkClass>()
				.validate(taxonomy);
	}

	@Test(expected = InvalidTaxonomyException.class)
	public void testSelfLoop() throws Exception {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = createEmptyTaxonomy();
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		ElkClass D = factory.getClass(new ElkFullIri("#D"));

		taxonomy.getCreateTypeNode(Arrays.asList(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B));
		taxonomy.getCreateTypeNode(Arrays.asList(C));
		taxonomy.getCreateTypeNode(Arrays.asList(D));

		taxonomy.getNode(B).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(C).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(B));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(C));
		// this one creates a self-loop
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(D));

		new TaxonomyAcyclicityAndReductionValidator<ElkClass>()
				.validate(taxonomy);
	}

	@Test(expected = InvalidTaxonomyException.class)
	public void testNonReduced() throws Exception {
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = createEmptyTaxonomy();
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		ElkClass D = factory.getClass(new ElkFullIri("#D"));

		taxonomy.getCreateTypeNode(Arrays.asList(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B));
		taxonomy.getCreateTypeNode(Arrays.asList(C));
		taxonomy.getCreateTypeNode(Arrays.asList(D));

		taxonomy.getNode(B).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(C).addDirectParent(taxonomy.getNode(A));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(B));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(C));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(A));

		new TaxonomyAcyclicityAndReductionValidator<ElkClass>()
				.validate(taxonomy);
	}

	private MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> createEmptyTaxonomy() {
		PredefinedElkClassFactory factory = new ElkObjectEntityRecyclingFactory();
		return new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				factory.getOwlThing(), factory.getOwlNothing(),
				ElkClassKeyProvider.INSTANCE,
				ElkIndividualKeyProvider.INSTANCE);
	}

	@SuppressWarnings("resource")
	private Taxonomy<ElkClass> load(String resource) throws IOException,
			Owl2ParseException, ElkInconsistentOntologyException {
		InputStream stream = null;

		try {
			stream = getClass().getClassLoader().getResourceAsStream(resource);
			ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();
			return MockTaxonomyLoader.load(objectFactory,
					new Owl2FunctionalStyleParserFactory(objectFactory)
							.getParser(stream));
		} finally {
			if (stream != null) {
				IOUtils.closeQuietly(stream);
			}
		}
	}
}
