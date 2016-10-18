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
package org.semanticweb.elk.reasoner.taxonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefixImpl;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;

/**
 * Tests whether members of various implementations of taxonomy
 * are compared according to their full IRI.
 * 
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class ObjectPropertyTaxonomyMemberComparisonTest {
	
	final static ElkObject.Factory OBJECT_FACTORY = new ElkObjectEntityRecyclingFactory();	

	static interface PropertyTaxonomyProvider {
		Taxonomy<ElkObjectProperty> getTaxonomy(String resource) throws ElkException;
	}
	
	/**
	 * Loads ontology from the <code>source</code>, classifies it with the reasoner
	 * and returns its taxonomy. This ensures that the returned taxonomy is the one used
	 * internally.
	 */
	static final PropertyTaxonomyProvider REASONER_TAXONOMY = new PropertyTaxonomyProvider() {
		@Override
		public Taxonomy<ElkObjectProperty> getTaxonomy(final String resource) throws ElkException {

			final Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					getClass().getClassLoader().getResourceAsStream(resource),
					new SimpleStageExecutor(), 1);

			return reasoner.getObjectPropertyTaxonomy();

		}
		@Override
		public String toString() {
			return "Reasoner Taxonomy";
		};
	};
	
	/**
	 * Loads mock taxonomy from the source.
	 */
	static final PropertyTaxonomyProvider MOCK_TAXONOMY = new PropertyTaxonomyProvider() {
		@Override
		public Taxonomy<ElkObjectProperty> getTaxonomy(final String resource) throws ElkException {
			
			InputStream stream = null;

			try {
				stream = getClass().getClassLoader().getResourceAsStream(resource);
				return MockObjectPropertyTaxonomyLoader.load(OBJECT_FACTORY,
						new Owl2FunctionalStyleParserFactory().getParser(stream));
			} finally {
				IOUtils.closeQuietly(stream);
			}
			
		}
		@Override
		public String toString() {
			return "Mock Taxonomy";
		}
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
	private final PropertyTaxonomyProvider propertyTaxonomyProvider_;
	
	public ObjectPropertyTaxonomyMemberComparisonTest(
			final PropertyTaxonomyProvider classTaxonomyProvider) {
		this.propertyTaxonomyProvider_ = classTaxonomyProvider;
	}
	
	@Test
	public void testNodeLookup() throws ElkException {
		final Taxonomy<ElkObjectProperty> taxonomy =
				propertyTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/property_node_lookup.owl");
		
		// IRI-s that look different, but are the same point to the same node
		
		final ElkObjectProperty sameA1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/same#A"));
		
		final ElkObjectProperty sameA2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "A"));
		
		final ElkObjectProperty sameA3 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "A"));
		
		final TaxonomyNode<ElkObjectProperty> nodeSameA1 = taxonomy.getNode(sameA1);
		final TaxonomyNode<ElkObjectProperty> nodeSameA2 = taxonomy.getNode(sameA2);
		final TaxonomyNode<ElkObjectProperty> nodeSameA3 = taxonomy.getNode(sameA3);
		
		assertEquals(nodeSameA1, nodeSameA2);
		assertEquals(nodeSameA1, nodeSameA3);
		
		// IRI-s that are different point to different nodes
		
		final ElkObjectProperty differentA1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/different#A"));
		
		final ElkObjectProperty differentA2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "A"));
		
		final TaxonomyNode<ElkObjectProperty> nodeDifferentA1 = taxonomy.getNode(differentA1);
		final TaxonomyNode<ElkObjectProperty> nodeDifferentA2 = taxonomy.getNode(differentA2);
		
		assertNotEquals(nodeSameA1, nodeDifferentA1);
		assertNotEquals(nodeSameA1, nodeDifferentA2);
		
		final ElkObjectProperty sameC1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/same#C"));
		
		final ElkObjectProperty sameC2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "C"));
		
		final ElkObjectProperty sameC3 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "C"));
		
		final TaxonomyNode<ElkObjectProperty> nodeSameC1 = taxonomy.getNode(sameC1);
		final TaxonomyNode<ElkObjectProperty> nodeSameC2 = taxonomy.getNode(sameC2);
		final TaxonomyNode<ElkObjectProperty> nodeSameC3 = taxonomy.getNode(sameC3);
		
		assertNotEquals(nodeSameA1, nodeSameC1);
		assertNotEquals(nodeSameA1, nodeSameC2);
		assertNotEquals(nodeSameA1, nodeSameC3);
		
	}
	
	@Test
	public void testMemberSet() throws ElkException {
		final Taxonomy<ElkObjectProperty> taxonomy =
				propertyTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/property_member_set.owl");
		
		final ElkObjectProperty sameA1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/same#A"));
		final ElkObjectProperty sameA2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "A"));
		final ElkObjectProperty sameA3 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "A"));
		final ElkObjectProperty differentA1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/different#A"));
		final ElkObjectProperty differentA2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "A"));
		
		final ElkObjectProperty sameB1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/same#B"));
		final ElkObjectProperty sameB2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "B"));
		final ElkObjectProperty sameB3 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "B"));
		final ElkObjectProperty differentB1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/different#B"));
		final ElkObjectProperty differentB2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "B"));
		
		final ElkObjectProperty sameC1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/same#C"));
		final ElkObjectProperty sameC2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "C"));
		final ElkObjectProperty sameC3 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "C"));
		final ElkObjectProperty differentC1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/different#C"));
		final ElkObjectProperty differentC2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "C"));
		
		// The same members are in the node only once
		
		final TaxonomyNode<ElkObjectProperty> nodeA = taxonomy.getNode(differentA1);
		
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
		
		final TaxonomyNode<ElkObjectProperty> nodeB = taxonomy.getNode(differentB1);
		
		assertTrue(nodeB.contains(differentB1));
		assertTrue(nodeB.contains(differentB2));
		assertFalse(nodeB.contains(sameB1));
		assertFalse(nodeB.contains(sameB2));
		assertFalse(nodeB.contains(sameB3));
		
		final TaxonomyNode<ElkObjectProperty> nodeC = taxonomy.getNode(sameC1);
		
		assertFalse(nodeC.contains(differentC1));
		assertFalse(nodeC.contains(differentC2));
		assertTrue(nodeC.contains(sameC1));
		assertTrue(nodeC.contains(sameC2));
		assertTrue(nodeC.contains(sameC3));
		
	}
	
	@Test
	public void testRelatedNodes() throws ElkException {
		final Taxonomy<ElkObjectProperty> taxonomy =
				propertyTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/property_related_nodes.owl");
		
		final ElkObjectProperty differentA2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "A"));
		
		final ElkObjectProperty sameB1 = OBJECT_FACTORY.getObjectProperty(
				new ElkFullIri("http://example.org/same#B"));
		final ElkObjectProperty differentB2 = OBJECT_FACTORY.getObjectProperty(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "B"));
		
		// Super-/sub-node is created only for different members
		
		final TaxonomyNode<ElkObjectProperty> nodeA = taxonomy.getNode(differentA2);

		final Set<? extends TaxonomyNode<ElkObjectProperty>> superA = nodeA.getDirectSuperNodes();
		
		assertEquals(1, superA.size());
		assertTrue(superA.iterator().next().contains(differentB2));
		assertFalse(superA.iterator().next().contains(sameB1));
		
		final Set<? extends TaxonomyNode<ElkObjectProperty>> subA = nodeA.getDirectSubNodes();
		assertEquals(2, subA.size());
		
	}
	
}
