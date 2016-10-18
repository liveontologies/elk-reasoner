package org.semanticweb.elk.reasoner.taxonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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

import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefixImpl;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;

/**
 * Tests whether members of various implementations of taxonomy
 * are compared according to their full IRI.
 * 
 * 
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class InstanceTaxonomyMemberComparisonTest {
	
	final static ElkObject.Factory OBJECT_FACTORY = new ElkObjectEntityRecyclingFactory();	

	static interface InstanceTaxonomyProvider {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy(String resource)
				throws ElkException;
	}
	
	/**
	 * Loads ontology from the <code>source</code>, classifies it with the reasoner
	 * and returns its taxonomy. This ensures that the returned taxonomy is the one used
	 * internally.
	 */
	static final InstanceTaxonomyProvider REASONER_TAXONOMY = new InstanceTaxonomyProvider() {
		@Override
		public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy(final String resource)
				throws ElkException {

			final Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					getClass().getClassLoader().getResourceAsStream(resource),
					new SimpleStageExecutor(), 1);

			return reasoner.getInstanceTaxonomy();

		}
		@Override
		public String toString() {
			return "Reasoner Taxonomy";
		};
	};
	
	/**
	 * Loads mock taxonomy from the source.
	 */
	static final InstanceTaxonomyProvider MOCK_TAXONOMY = new InstanceTaxonomyProvider() {
		@Override
		public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy(final String resource)
				throws ElkException {
			
			InputStream stream = null;

			try {
				stream = getClass().getClassLoader().getResourceAsStream(resource);
				return MockTaxonomyLoader.load(OBJECT_FACTORY,
						new Owl2FunctionalStyleParserFactory().getParser(stream));
			} finally {
				IOUtils.closeQuietly(stream);
			}
			
		}
		@Override
		public String toString() {
			return "Mock Taxonomy";
		};
	};
	
	static final Object[] DATA = {
			REASONER_TAXONOMY,
			MOCK_TAXONOMY,
	};
	
	static final Configuration CONFIG = new Configuration() {
		@Override
		public int size() {
			return DATA.length;
		}
		@Override
		public Object getTestValue(final int index) {
			return DATA[index];
		}
		@Override
		public String getTestName(final int index) {
			return "test on " + DATA[index].toString();
		}
	};
	
	@Config
	public static Configuration getConfig() {
		return CONFIG;
	}
	
	/**
	 * Provides taxonomy that should be tested.
	 */
	private final InstanceTaxonomyProvider instanceTaxonomyProvider_;
	
	public InstanceTaxonomyMemberComparisonTest(
			final InstanceTaxonomyProvider instanceTaxonomyProvider) {
		this.instanceTaxonomyProvider_ = instanceTaxonomyProvider;
	}
	
	@Test
	public void testNodeLookup() throws ElkException {
		final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy =
				instanceTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/instance_node_lookup.owl");
		
		// IRI-s that look different, but are the same point to the same node
		
		final ElkNamedIndividual sameA1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/same#a"));
		
		final ElkNamedIndividual sameA2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "a"));
		
		final ElkNamedIndividual sameA3 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "a"));
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameA1 =
				taxonomy.getInstanceNode(sameA1);
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameA2 =
				taxonomy.getInstanceNode(sameA2);
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameA3 =
				taxonomy.getInstanceNode(sameA3);
		
		assertSame(nodeSameA1, nodeSameA2);
		assertSame(nodeSameA1, nodeSameA3);
		
		// IRI-s that are different point to different nodes
		
		final ElkNamedIndividual differentA1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/different#a"));
		
		final ElkNamedIndividual differentA2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "a"));
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeDifferentA1 =
				taxonomy.getInstanceNode(differentA1);
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeDifferentA2 =
				taxonomy.getInstanceNode(differentA2);
		
		assertNotSame(nodeSameA1, nodeDifferentA1);
		assertNotSame(nodeSameA1, nodeDifferentA2);
		
		final ElkNamedIndividual sameC1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/same#c"));
		
		final ElkNamedIndividual sameC2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "c"));
		
		final ElkNamedIndividual sameC3 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "c"));
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameC1 =
				taxonomy.getInstanceNode(sameC1);
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameC2 =
				taxonomy.getInstanceNode(sameC2);
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameC3 =
				taxonomy.getInstanceNode(sameC3);
		
		assertNotSame(nodeSameA1, nodeSameC1);
		assertNotSame(nodeSameA1, nodeSameC2);
		assertNotSame(nodeSameA1, nodeSameC3);
		
	}
	
	@Test
	@Ignore// TODO: read comment in org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomyComputationFactory
	public void testMemberSet() throws ElkException {
		final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy =
				instanceTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/instance_member_set.owl");
		
		final ElkNamedIndividual sameA1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/same#a"));
		final ElkNamedIndividual sameA2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "a"));
		final ElkNamedIndividual sameA3 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "a"));
		final ElkNamedIndividual differentA1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/different#a"));
		final ElkNamedIndividual differentA2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "a"));
		
		final ElkNamedIndividual sameB1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/same#b"));
		final ElkNamedIndividual sameB2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "b"));
		final ElkNamedIndividual sameB3 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "b"));
		final ElkNamedIndividual differentB1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/different#b"));
		final ElkNamedIndividual differentB2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "b"));
		
		final ElkNamedIndividual sameC1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/same#c"));
		final ElkNamedIndividual sameC2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "c"));
		final ElkNamedIndividual sameC3 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "c"));
		final ElkNamedIndividual differentC1 = OBJECT_FACTORY.getNamedIndividual(
				new ElkFullIri("http://example.org/different#c"));
		final ElkNamedIndividual differentC2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "c"));
		
		// The same members are in the node only once
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeA =
				taxonomy.getInstanceNode(differentA1);
		
		assertEquals(3, nodeA.size());
		assertTrue(nodeA.contains(differentA1));
		assertTrue(nodeA.contains(differentA2));
		assertTrue(nodeA.contains(sameA1));
		assertTrue(nodeA.contains(sameA2));
		assertTrue(nodeA.contains(sameA3));
		assertFalse(nodeA.contains(differentB1));
		assertFalse(nodeA.contains(differentB2));
		assertTrue(nodeA.contains(sameB1));
		assertTrue(nodeA.contains(sameB2));
		assertTrue(nodeA.contains(sameB3));
		
		// Node does not contain members that should be different
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeB =
				taxonomy.getInstanceNode(differentB1);
		
		assertTrue(nodeB.contains(differentB1));
		assertTrue(nodeB.contains(differentB2));
		assertFalse(nodeB.contains(sameB1));
		assertFalse(nodeB.contains(sameB2));
		assertFalse(nodeB.contains(sameB3));
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeC =
				taxonomy.getInstanceNode(sameC1);
		
		assertFalse(nodeC.contains(differentC1));
		assertFalse(nodeC.contains(differentC2));
		assertTrue(nodeC.contains(sameC1));
		assertTrue(nodeC.contains(sameC2));
		assertTrue(nodeC.contains(sameC3));
		
	}
	
	@Test
	public void testRelatedNodes() throws ElkException {
		final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy =
				instanceTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/instance_related_nodes.owl");
		
		// The same type nodes are the same.
		
		final ElkNamedIndividual differentA2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "a"));
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeDifferentA =
				taxonomy.getInstanceNode(differentA2);
		
		assertEquals(1, nodeDifferentA.getDirectTypeNodes().size());
		
		// The different type nodes are different.
		
		final ElkNamedIndividual sameA2 = OBJECT_FACTORY.getNamedIndividual(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "a"));
		
		final InstanceNode<ElkClass, ElkNamedIndividual> nodeSameA =
				taxonomy.getInstanceNode(sameA2);
		
		assertEquals(2, nodeSameA.getDirectTypeNodes().size());
		
		// The same instance nodes are the same.
		
		final ElkClass differentB2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "B"));
		
		final TypeNode<ElkClass, ElkNamedIndividual> nodeB = taxonomy.getNode(differentB2);
		
		assertEquals(2, nodeB.getDirectInstanceNodes().size());
		
		// The different instance nodes are different.
		
		final ElkClass differentC2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "C"));
		
		final TypeNode<ElkClass, ElkNamedIndividual> nodeC = taxonomy.getNode(differentC2);
		
		assertEquals(2, nodeC.getDirectInstanceNodes().size());
		
	}
	
}
