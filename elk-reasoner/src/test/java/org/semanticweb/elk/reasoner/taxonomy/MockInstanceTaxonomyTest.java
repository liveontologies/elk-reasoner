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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;

/**
 * Some basic tests for the mock instance taxonomy (which is itself primarily
 * used in tests)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MockInstanceTaxonomyTest {

	@SuppressWarnings("static-method")
	@Test
	public void testTopBotEquivalence() {
		ElkClass.Factory factory = new ElkObjectEntityRecyclingFactory();// we reuse iri objects
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				factory.getOwlThing(), factory.getOwlNothing(),
				ElkClassKeyProvider.INSTANCE,
				ElkIndividualKeyProvider.INSTANCE);

		ElkClass A = factory.getClass(new ElkFullIri("#A"));

		taxonomy.getCreateTypeNode(Collections.singleton(A));
		taxonomy.getTopNode().addDirectParent(taxonomy.getNode(A));

		assertSame(taxonomy.getTopNode(), taxonomy.getNode(A));
		assertEquals(2, taxonomy.getTopNode().size());
		assertEquals(2, taxonomy.getNodes().size());
		assertEquals(2, taxonomy.getNodes().size());

		ElkClass B = factory.getClass(new ElkFullIri("#B"));

		taxonomy.getCreateTypeNode(Collections.singleton(B));
		taxonomy.getNode(B).addDirectParent(taxonomy.getBottomNode());

		assertSame(taxonomy.getBottomNode(), taxonomy.getNode(B));
		assertEquals(2, taxonomy.getBottomNode().size());
		assertEquals(2, taxonomy.getNodes().size());
		assertEquals(2, taxonomy.getNodes().size());
	}

	@SuppressWarnings("static-method")
	@Test
	public void testForDuplicates() {
		ElkEntity.Factory factory = new ElkObjectEntityRecyclingFactory();// we reuse iri objects
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				factory.getOwlThing(), factory.getOwlNothing(),
				ElkClassKeyProvider.INSTANCE,
				ElkIndividualKeyProvider.INSTANCE);

		ElkClass A1 = factory.getClass(new ElkFullIri("#A"));
		ElkClass A2 = factory.getClass(new ElkFullIri("#A"));
		ElkNamedIndividual a1 = factory
				.getNamedIndividual(new ElkFullIri("#a"));
		ElkNamedIndividual a2 = factory
				.getNamedIndividual(new ElkFullIri("#a"));

		taxonomy.getCreateTypeNode(Collections.singleton(A1));
		taxonomy.getCreateTypeNode(Collections.singleton(A2));

		assertSame(taxonomy.getNode(A1), taxonomy.getNode(A2));

		taxonomy.getCreateInstanceNode(Collections.singleton(a1), Collections
				.<TypeNode<ElkClass, ElkNamedIndividual>> singleton(taxonomy
						.getNode(A1)));
		taxonomy.getCreateInstanceNode(Collections.singleton(a2), Collections
				.<TypeNode<ElkClass, ElkNamedIndividual>> singleton(taxonomy
						.getNode(A2)));

		assertSame(taxonomy.getInstanceNode(a1), taxonomy.getInstanceNode(a2));
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "static-method" })
	@Test
	public void testMockTaxonomy() {
		ElkEntity.Factory factory = new ElkObjectEntityRecyclingFactory();// we reuse iri objects
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				factory.getOwlThing(), factory.getOwlNothing(),
				ElkClassKeyProvider.INSTANCE,
				ElkIndividualKeyProvider.INSTANCE);

		assertSame(factory.getOwlThing(), taxonomy.getTopNode()
				.getCanonicalMember());
		assertSame(factory.getOwlNothing(), taxonomy.getBottomNode()
				.getCanonicalMember());
		// check subclass relationships between Top and Bot
		assertSame(taxonomy.getTopNode(), taxonomy.getBottomNode()
				.getDirectSuperNodes().iterator().next());
		assertSame(taxonomy.getBottomNode(), taxonomy.getTopNode()
				.getAllSubNodes().iterator().next());
		// add some nodes
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));

		taxonomy.getCreateTypeNode(Collections.singleton(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B, C));

		assertEquals(4, taxonomy.getNodes().size());
		assertSame(taxonomy.getNode(B), taxonomy.getNode(C));
		// check that Top and Bot are returned if there're no other
		// parents/children
		assertEquals(Collections.singleton(taxonomy.getTopNode()), taxonomy
				.getNode(A).getDirectSuperNodes());
		assertEquals(Collections.singleton(taxonomy.getBottomNode()), taxonomy
				.getNode(B).getDirectSubNodes());
		// add some child to A
		ElkClass D = factory.getClass(new ElkFullIri("#D"));

		taxonomy.getCreateTypeNode(Collections.singleton(D));
		taxonomy.getNode(D).addDirectParent(taxonomy.getNode(A));

		assertEquals(Collections.singleton(taxonomy.getNode(A)), taxonomy
				.getNode(D).getDirectSuperNodes());
		assertEquals(Collections.singleton(taxonomy.getNode(D)), taxonomy
				.getNode(A).getDirectSubNodes());

		assertEquals(
				new HashSet(Arrays.asList(taxonomy.getNode(A),
						taxonomy.getNode(B))), taxonomy.getTopNode()
						.getDirectSubNodes());
		assertEquals(
				new HashSet(Arrays.asList(taxonomy.getNode(D),
						taxonomy.getNode(B))), taxonomy.getBottomNode()
						.getDirectSuperNodes());
		// instances
		ElkNamedIndividual a = factory.getNamedIndividual(new ElkFullIri("#a"));
		ElkNamedIndividual b = factory.getNamedIndividual(new ElkFullIri("#b"));
		ElkNamedIndividual c = factory.getNamedIndividual(new ElkFullIri("#c"));
		ElkNamedIndividual d = factory.getNamedIndividual(new ElkFullIri("#d"));

		taxonomy.getCreateInstanceNode(Arrays.asList(a, b),
				Collections.<TypeNode<ElkClass, ElkNamedIndividual>> emptySet());
		// should be instances of Top
		assertEquals(new HashSet(Arrays.asList(taxonomy.getInstanceNode(a))),
				taxonomy.getTopNode().getDirectInstanceNodes());
		// the other way
		assertSame(taxonomy.getTopNode(), taxonomy.getInstanceNode(a)
				.getDirectTypeNodes().iterator().next());

		taxonomy.getCreateInstanceNode(Arrays.asList(c), Arrays
				.<TypeNode<ElkClass, ElkNamedIndividual>> asList(taxonomy
						.getNode(A)));
		taxonomy.getCreateInstanceNode(Arrays.asList(d), Arrays
				.<TypeNode<ElkClass, ElkNamedIndividual>> asList(taxonomy
						.getNode(D)));
		// check types/instances
		assertEquals(new HashSet(Arrays.asList(taxonomy.getInstanceNode(c))),
				taxonomy.getNode(A).getDirectInstanceNodes());
		assertEquals(
				new HashSet(Arrays.asList(taxonomy.getInstanceNode(c),
						taxonomy.getInstanceNode(d))), taxonomy.getNode(A)
						.getAllInstanceNodes());
		assertEquals(
				new HashSet(Arrays.asList(taxonomy.getNode(A),
						taxonomy.getNode(D))), taxonomy.getInstanceNode(d)
						.getAllTypeNodes());
	}
}