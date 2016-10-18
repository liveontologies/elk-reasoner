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
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
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
 * 
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class ClassTaxonomyMemberComparisonTest {
	
	final static ElkObject.Factory OBJECT_FACTORY = new ElkObjectEntityRecyclingFactory();	

	static interface ClassTaxonomyProvider {
		Taxonomy<ElkClass> getTaxonomy(String resource) throws ElkException;
	}
	
	/**
	 * Loads ontology from the <code>source</code>, classifies it with the reasoner
	 * and returns its taxonomy. This ensures that the returned taxonomy is the one used
	 * internally.
	 */
	static final ClassTaxonomyProvider REASONER_TAXONOMY = new ClassTaxonomyProvider() {
		@Override
		public Taxonomy<ElkClass> getTaxonomy(final String resource) throws ElkException {

			final Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					getClass().getClassLoader().getResourceAsStream(resource),
					new SimpleStageExecutor(), 1);

			return reasoner.getTaxonomy();

		}
		@Override
		public String toString() {
			return "Reasoner Taxonomy";
		};
	};
	
	/**
	 * Loads mock taxonomy from the source.
	 */
	static final ClassTaxonomyProvider MOCK_TAXONOMY = new ClassTaxonomyProvider() {
		@Override
		public Taxonomy<ElkClass> getTaxonomy(final String resource) throws ElkException {
			
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
	private final ClassTaxonomyProvider classTaxonomyProvider_;
	
	public ClassTaxonomyMemberComparisonTest(final ClassTaxonomyProvider classTaxonomyProvider) {
		this.classTaxonomyProvider_ = classTaxonomyProvider;
	}
	
	@Test
	public void testNodeLookup() throws ElkException {
		final Taxonomy<ElkClass> taxonomy =
				classTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/class_node_lookup.owl");
		
		// IRI-s that look different, but are the same point to the same node
		
		final ElkClass sameA1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/same#A"));
		
		final ElkClass sameA2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "A"));
		
		final ElkClass sameA3 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "A"));
		
		final TaxonomyNode<ElkClass> nodeSameA1 = taxonomy.getNode(sameA1);
		final TaxonomyNode<ElkClass> nodeSameA2 = taxonomy.getNode(sameA2);
		final TaxonomyNode<ElkClass> nodeSameA3 = taxonomy.getNode(sameA3);
		
		assertSame(nodeSameA1, nodeSameA2);
		assertSame(nodeSameA1, nodeSameA3);
		
		// IRI-s that are different point to different nodes
		
		final ElkClass differentA1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/different#A"));
		
		final ElkClass differentA2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "A"));
		
		final TaxonomyNode<ElkClass> nodeDifferentA1 = taxonomy.getNode(differentA1);
		final TaxonomyNode<ElkClass> nodeDifferentA2 = taxonomy.getNode(differentA2);
		
		assertNotSame(nodeSameA1, nodeDifferentA1);
		assertNotSame(nodeSameA1, nodeDifferentA2);
		
		final ElkClass sameC1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/same#C"));
		
		final ElkClass sameC2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "C"));
		
		final ElkClass sameC3 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "C"));
		
		final TaxonomyNode<ElkClass> nodeSameC1 = taxonomy.getNode(sameC1);
		final TaxonomyNode<ElkClass> nodeSameC2 = taxonomy.getNode(sameC2);
		final TaxonomyNode<ElkClass> nodeSameC3 = taxonomy.getNode(sameC3);
		
		assertNotSame(nodeSameA1, nodeSameC1);
		assertNotSame(nodeSameA1, nodeSameC2);
		assertNotSame(nodeSameA1, nodeSameC3);
		
	}
	
	@Test
	public void testMemberSet() throws ElkException {
		final Taxonomy<ElkClass> taxonomy =
				classTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/class_member_set.owl");
		
		final ElkClass sameA1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/same#A"));
		final ElkClass sameA2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "A"));
		final ElkClass sameA3 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "A"));
		final ElkClass differentA1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/different#A"));
		final ElkClass differentA2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "A"));
		
		final ElkClass sameB1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/same#B"));
		final ElkClass sameB2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "B"));
		final ElkClass sameB3 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "B"));
		final ElkClass differentB1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/different#B"));
		final ElkClass differentB2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "B"));
		
		final ElkClass sameC1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/same#C"));
		final ElkClass sameC2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same1:",
						new ElkFullIri("http://example.org/same#")), "C"));
		final ElkClass sameC3 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("same2:",
						new ElkFullIri("http://example.org/same#")), "C"));
		final ElkClass differentC1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/different#C"));
		final ElkClass differentC2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "C"));
		
		// The same members are in the node only once
		
		final TaxonomyNode<ElkClass> nodeA = taxonomy.getNode(differentA1);
		
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
		
		final TaxonomyNode<ElkClass> nodeB = taxonomy.getNode(differentB1);
		
		assertTrue(nodeB.contains(differentB1));
		assertTrue(nodeB.contains(differentB2));
		assertFalse(nodeB.contains(sameB1));
		assertFalse(nodeB.contains(sameB2));
		assertFalse(nodeB.contains(sameB3));
		
		final TaxonomyNode<ElkClass> nodeC = taxonomy.getNode(sameC1);
		
		assertFalse(nodeC.contains(differentC1));
		assertFalse(nodeC.contains(differentC2));
		assertTrue(nodeC.contains(sameC1));
		assertTrue(nodeC.contains(sameC2));
		assertTrue(nodeC.contains(sameC3));
		
	}
	
	@Test
	public void testRelatedNodes() throws ElkException {
		final Taxonomy<ElkClass> taxonomy =
				classTaxonomyProvider_.getTaxonomy("taxonomy_member_comparison/class_related_nodes.owl");
		
		final ElkClass differentA2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "A"));
		
		final ElkClass sameB1 = OBJECT_FACTORY.getClass(
				new ElkFullIri("http://example.org/same#B"));
		final ElkClass differentB2 = OBJECT_FACTORY.getClass(
				new ElkAbbreviatedIri(new ElkPrefixImpl("different:",
						new ElkFullIri("http://example.org/different#")), "B"));
		
		// Super-/sub-node is created only for different members
		
		final TaxonomyNode<ElkClass> nodeA = taxonomy.getNode(differentA2);

		final Set<? extends TaxonomyNode<ElkClass>> superA = nodeA.getDirectSuperNodes();
		
		assertEquals(1, superA.size());
		assertTrue(superA.iterator().next().contains(differentB2));
		assertFalse(superA.iterator().next().contains(sameB1));
		
		final Set<? extends TaxonomyNode<ElkClass>> subA = nodeA.getDirectSubNodes();
		assertEquals(2, subA.size());
		
	}
	
}
