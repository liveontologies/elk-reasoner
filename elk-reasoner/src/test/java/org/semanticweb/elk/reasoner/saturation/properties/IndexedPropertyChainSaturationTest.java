/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.properties;
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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectsCreator;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.VerifySymmetricPropertySaturation.AsymmetricCompositionHook;

/**
 * Tests for saturation of indexed property chains
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedPropertyChainSaturationTest {

	
	@Test
	@Ignore 
	public void testPropertyCompositionSymmetry() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		IndexedObjectProperty R1 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/R1")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);		
		IndexedObjectProperty R2 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/R2")), new IndexedPropertyChain[]{R1}, new IndexedObjectProperty[]{}, false);
		IndexedObjectProperty R3 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/R3")), new IndexedPropertyChain[]{R2}, new IndexedObjectProperty[]{}, false);
		
		IndexedPropertyChain R1R2 = IndexedObjectsCreator.createIndexedChain(R1, R2, new IndexedObjectProperty[]{R1, R3});
		IndexedPropertyChain R1R3 = IndexedObjectsCreator.createIndexedChain(R1, R3, new IndexedObjectProperty[]{R1, R3});
		IndexedPropertyChain R2R3 = IndexedObjectsCreator.createIndexedChain(R2, R3, new IndexedObjectProperty[]{R1, R3});
		
		List<IndexedPropertyChain> chains = Arrays.asList(R1, R2, R3, R1R2, R1R3, R2R3);
		
		AsymmetricCompositionHook hook = new AsymmetricCompositionHook() {

			@Override
			public void error(IndexedPropertyChain left,
					IndexedPropertyChain right,
					IndexedPropertyChain composition,
					IndexedPropertyChain computed) {
				Assert.fail("Composition " + left + " o "
						+ right + " => " + composition
						+ " is computed for " + (left == computed ? left : right) + " but not for "
						+ (left == computed ? right : left));				
			}
		};
		
		for (int i = 1; i < 10; i++) {
			Collections.shuffle(chains);
			
			for (IndexedPropertyChain chain : chains) {
				VerifySymmetricPropertySaturation.testLeftCompositions(chain, hook);
				VerifySymmetricPropertySaturation.testRightCompositions(chain, hook);
			}
		}
	}
	
	/**
	 * R is composable with itself, this test checks that
	 * computation of compositionsByLeftSubProperty and compositionsByRightSubProperty
	 * work correctly and don't fall into an infinite cycle
	 */
	@Test
	public void testCyclicCompositions() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		
		IndexedObjectProperty H = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/H")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);		
		IndexedObjectProperty S3 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/S3")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		IndexedObjectProperty S2 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/S2")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{S3}, false);
		IndexedObjectProperty S1 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/S1")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{S2}, false);
		IndexedObjectProperty P3 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/P3")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		IndexedObjectProperty P2 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/P2")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{P3}, false);
		IndexedObjectProperty P1 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/P1")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{P2}, false);
		//R is a super-property of S3, which creates a loop R -> S1 -> S2 -> S3 -> R
		IndexedObjectProperty R = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/R")), new IndexedPropertyChain[]{S3}, new IndexedObjectProperty[]{S1, P1}, false);
		
		IndexedObjectsCreator.createIndexedChain(R, R, new IndexedObjectProperty[]{H});
		
		SaturatedPropertyChain saturated = IndexedPropertyChainSaturation.saturate(R);
		
		assertTrue(saturated.getCompositionsByLeftSubProperty().get(R).contains(H));
		assertTrue(saturated.getCompositionsByRightSubProperty().get(R).contains(H));
	}
	
	/**
	 * Test method for {@link ReflexivityChecker#isReflexive()}.
	 */
	@Test
	public void testIsReflexive() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		List<IndexedPropertyChain> chains = new ArrayList<IndexedPropertyChain>();
		
		IndexedObjectProperty t = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/T")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		IndexedObjectProperty r1 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/R1")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, true);
		IndexedObjectProperty r = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/R")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		//r1 o r1 -> r, thus r must be reflexive
		IndexedPropertyChain r1r1 = IndexedObjectsCreator.createIndexedChain(r1, r1, new IndexedObjectProperty[]{r});		
		
		IndexedPropertyChain rt = IndexedObjectsCreator.createIndexedChain(r, t, new IndexedObjectProperty[]{});
		// r o t -> u
		IndexedObjectProperty u = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/U")), new IndexedPropertyChain[]{rt}, new IndexedObjectProperty[]{}, false);
		
		IndexedObjectProperty h = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/H")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, true);
		// h -> h1, thus h1 must be reflexive
		IndexedObjectProperty h1 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/H1")), new IndexedPropertyChain[]{h}, new IndexedObjectProperty[]{}, false);
		IndexedObjectProperty s = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/S")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		// r o h1 -> s, both are implicitly reflexive, thus s must be reflexive
		IndexedPropertyChain rh1 = IndexedObjectsCreator.createIndexedChain(r, h1, new IndexedObjectProperty[]{s});
		//finally, add some loops
		IndexedObjectProperty v1 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/V1")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		IndexedObjectProperty v2 = IndexedObjectsCreator.createIndexedObjectProperty(factory.getObjectProperty(new ElkFullIri("http://test.com/V2")), new IndexedPropertyChain[]{}, new IndexedObjectProperty[]{}, false);
		IndexedPropertyChain v1v1 = IndexedObjectsCreator.createIndexedChain(v1, v1, new IndexedObjectProperty[]{v2});
		IndexedPropertyChain v2v2 = IndexedObjectsCreator.createIndexedChain(v2, v2, new IndexedObjectProperty[]{v1});
		
		chains.addAll(Arrays.asList(new IndexedPropertyChain[]{t, r1, r, r1r1, rt, u, h, h1, s, rh1, v1, v2, v1v1, v2v2}));
		
		Set<IndexedPropertyChain> correctReflexive = new HashSet<IndexedPropertyChain>(Arrays.asList(new IndexedPropertyChain[]{r, h, h1, r1, r1r1, rh1, s}));
		Set<IndexedPropertyChain> correctNonreflexive = new HashSet<IndexedPropertyChain>(Arrays.asList(new IndexedPropertyChain[]{t, u, rt, v1, v2, v1v1, v2v2}));
		
		for (int i = 0; i < 100; i++) {
			//emulate arbitrary order of indexing properties
			Collections.shuffle(chains);
			
			Set<IndexedPropertyChain> reflexive = new HashSet<IndexedPropertyChain>();
			Set<IndexedPropertyChain> nonreflexive = new HashSet<IndexedPropertyChain>();			
			
			for (IndexedPropertyChain chain : chains) {
				
				chain.setSaturated(null);
				
				if (new ReflexivityCheckVisitor().isReflexive(chain)) {
					reflexive.add(chain);
				}
				else {
					nonreflexive.add(chain);
				}
			}
			
			assertEquals(correctReflexive, reflexive);
			assertEquals(correctNonreflexive, nonreflexive);
		}
	}
}