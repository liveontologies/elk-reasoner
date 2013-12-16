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
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkEntityRecycler;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.util.Comparators;
import org.semanticweb.elk.reasoner.taxonomy.nodes.TypeNode;

/**
 * Some basic tests for the mock instance taxonomy (which is itself primarily used in tests)
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MockInstanceTaxonomyTest {

	@Test
	public void testTopBotEquivalence() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl(
				new ElkEntityRecycler());// we reuse iri objects
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				PredefinedElkClass.OWL_THING, PredefinedElkClass.OWL_NOTHING,
				Comparators.ELK_CLASS_COMPARATOR,
				Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR);
		
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		
		taxonomy.getCreateTypeNode(Collections.singleton(A));
		taxonomy.getTopNode().addDirectParent(taxonomy.getTypeNode(A));
		
		assertSame(taxonomy.getTopNode(), taxonomy.getTypeNode(A));
		assertEquals(2, taxonomy.getTopNode().getMembers().size());
		assertEquals(2, taxonomy.getTypeNodes().size());
		assertEquals(2, taxonomy.getNodes().size());
		
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		
		taxonomy.getCreateTypeNode(Collections.singleton(B));
		taxonomy.getTypeNode(B).addDirectParent(taxonomy.getBottomNode());
		
		assertSame(taxonomy.getBottomNode(), taxonomy.getTypeNode(B));
		assertEquals(2, taxonomy.getBottomNode().getMembers().size());
		assertEquals(2, taxonomy.getTypeNodes().size());
		assertEquals(2, taxonomy.getNodes().size());		
	}
	
	@Test
	public void testForDuplicates() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl(
				new ElkEntityRecycler());// we reuse iri objects
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				PredefinedElkClass.OWL_THING, PredefinedElkClass.OWL_NOTHING,
				Comparators.ELK_CLASS_COMPARATOR,
				Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR);
		
		ElkClass A1 = factory.getClass(new ElkFullIri("#A"));
		ElkClass A2 = factory.getClass(new ElkFullIri("#A"));
		ElkNamedIndividual a1 = factory.getNamedIndividual(new ElkFullIri("#a"));
		ElkNamedIndividual a2 = factory.getNamedIndividual(new ElkFullIri("#a"));
		
		taxonomy.getCreateTypeNode(Collections.singleton(A1));
		taxonomy.getCreateTypeNode(Collections.singleton(A2));
		
		assertSame(taxonomy.getTypeNode(A1), taxonomy.getTypeNode(A2));
		
		taxonomy.getCreateInstanceNode(Collections.singleton(a1), Collections.<TypeNode<ElkClass,ElkNamedIndividual>>singleton(taxonomy.getTypeNode(A1)));
		taxonomy.getCreateInstanceNode(Collections.singleton(a2), Collections.<TypeNode<ElkClass,ElkNamedIndividual>>singleton(taxonomy.getTypeNode(A2)));
		
		assertSame(taxonomy.getInstanceNode(a1), taxonomy.getInstanceNode(a2));
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMockTaxonomy() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl(
				new ElkEntityRecycler());// we reuse iri objects
		MockInstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = new MockInstanceTaxonomy<ElkClass, ElkNamedIndividual>(
				PredefinedElkClass.OWL_THING, PredefinedElkClass.OWL_NOTHING,
				Comparators.ELK_CLASS_COMPARATOR,
				Comparators.ELK_NAMED_INDIVIDUAL_COMPARATOR);
		
		assertSame(PredefinedElkClass.OWL_THING, taxonomy.getTopNode().getCanonicalMember());
		assertSame(PredefinedElkClass.OWL_NOTHING, taxonomy.getBottomNode().getCanonicalMember());
		//check subclass relationships between Top and Bot
		assertSame(taxonomy.getTopNode(), taxonomy.getBottomNode().getDirectSuperNodes().iterator().next());		
		assertSame(taxonomy.getBottomNode(), taxonomy.getTopNode().getAllSubNodes().iterator().next());
		//add some nodes
		ElkClass A = factory.getClass(new ElkFullIri("#A"));
		ElkClass B = factory.getClass(new ElkFullIri("#B"));
		ElkClass C = factory.getClass(new ElkFullIri("#C"));
		
		taxonomy.getCreateTypeNode(Collections.singleton(A));
		taxonomy.getCreateTypeNode(Arrays.asList(B,C));
		
		assertEquals(4, taxonomy.getTypeNodes().size());
		assertSame(taxonomy.getTypeNode(B), taxonomy.getTypeNode(C));
		//check that Top and Bot are returned if there're no other parents/children
		assertEquals(Collections.singleton(taxonomy.getTopNode()), taxonomy.getTypeNode(A).getDirectSuperNodes());
		assertEquals(Collections.singleton(taxonomy.getBottomNode()), taxonomy.getTypeNode(B).getDirectSubNodes());
		//add some child to A
		ElkClass D = factory.getClass(new ElkFullIri("#D"));

		taxonomy.getCreateTypeNode(Collections.singleton(D));
		taxonomy.getTypeNode(D).addDirectParent(taxonomy.getTypeNode(A));
		
		assertEquals(Collections.singleton(taxonomy.getTypeNode(A)), taxonomy.getTypeNode(D).getDirectSuperNodes());
		assertEquals(Collections.singleton(taxonomy.getTypeNode(D)), taxonomy.getTypeNode(A).getDirectSubNodes());
		
		assertEquals(new HashSet(Arrays.asList(taxonomy.getTypeNode(A), taxonomy.getTypeNode(B))), taxonomy.getTopNode().getDirectSubNodes());
		assertEquals(new HashSet(Arrays.asList(taxonomy.getTypeNode(D), taxonomy.getTypeNode(B))), taxonomy.getBottomNode().getDirectSuperNodes());
		//instances
		ElkNamedIndividual a = factory.getNamedIndividual(new ElkFullIri("#a"));
		ElkNamedIndividual b = factory.getNamedIndividual(new ElkFullIri("#b"));
		ElkNamedIndividual c = factory.getNamedIndividual(new ElkFullIri("#c"));
		ElkNamedIndividual d = factory.getNamedIndividual(new ElkFullIri("#d"));
		
		taxonomy.getCreateInstanceNode(Arrays.asList(a, b), Collections.<TypeNode<ElkClass,ElkNamedIndividual>>emptySet());
		//should be instances of Top
		assertEquals(new HashSet(Arrays.asList(taxonomy.getInstanceNode(a))), taxonomy.getTopNode().getDirectInstanceNodes());
		//the other way
		assertSame(taxonomy.getTopNode(), taxonomy.getInstanceNode(a).getDirectTypeNodes().iterator().next());
		
		taxonomy.getCreateInstanceNode(Arrays.asList(c), Arrays.<TypeNode<ElkClass,ElkNamedIndividual>>asList(taxonomy.getTypeNode(A)));
		taxonomy.getCreateInstanceNode(Arrays.asList(d), Arrays.<TypeNode<ElkClass,ElkNamedIndividual>>asList(taxonomy.getTypeNode(D)));
		//check types/instances
		assertEquals(new HashSet(Arrays.asList(taxonomy.getInstanceNode(c))), taxonomy.getTypeNode(A).getDirectInstanceNodes());
		assertEquals(new HashSet(Arrays.asList(taxonomy.getInstanceNode(c), taxonomy.getInstanceNode(d))), taxonomy.getTypeNode(A).getAllInstanceNodes());
		assertEquals(new HashSet(Arrays.asList(taxonomy.getTypeNode(A), taxonomy.getTypeNode(D))), taxonomy.getInstanceNode(d).getAllTypeNodes());
	}
}