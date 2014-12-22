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
import org.semanticweb.elk.reasoner.DummyProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.implementation.IndexedObjectsCreator;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.VerifySymmetricPropertySaturation.AsymmetricCompositionHook;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * Tests for saturation of indexed property chains
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedPropertyChainSaturationTest {

	@SuppressWarnings("static-method")
	@Test
	@Ignore
	public void testPropertyCompositionSymmetry() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ModifiableIndexedObjectProperty R1 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty R2 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R2")),
						new ModifiableIndexedPropertyChain[] { R1 },
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty R3 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R3")),
						new ModifiableIndexedPropertyChain[] { R2 },
						new ModifiableIndexedObjectProperty[] {}, false);

		ModifiableIndexedPropertyChain R1R2 = IndexedObjectsCreator
				.createIndexedChain(R1, R2,
						new ModifiableIndexedObjectProperty[] { R1, R3 });
		ModifiableIndexedPropertyChain R1R3 = IndexedObjectsCreator
				.createIndexedChain(R1, R3,
						new ModifiableIndexedObjectProperty[] { R1, R3 });
		ModifiableIndexedPropertyChain R2R3 = IndexedObjectsCreator
				.createIndexedChain(R2, R3,
						new ModifiableIndexedObjectProperty[] { R1, R3 });

		List<ModifiableIndexedPropertyChain> chains = Arrays.asList(R1, R2, R3,
				R1R2, R1R3, R2R3);

		AsymmetricCompositionHook hook = new AsymmetricCompositionHook() {

			@Override
			public void error(IndexedPropertyChain left,
					IndexedPropertyChain right,
					IndexedPropertyChain composition,
					IndexedPropertyChain computed) {
				Assert.fail("Composition " + left + " o " + right + " => "
						+ composition + " is computed for "
						+ (left == computed ? left : right) + " but not for "
						+ (left == computed ? right : left));
			}
		};

		for (int i = 1; i < 10; i++) {
			Collections.shuffle(chains);

			for (IndexedPropertyChain chain : chains) {
				VerifySymmetricPropertySaturation.testLeftCompositions(chain,
						hook);
				if (chain instanceof IndexedObjectProperty)
					VerifySymmetricPropertySaturation.testRightCompositions(
							(IndexedObjectProperty) chain, hook);
			}
		}
	}

	/**
	 * R is composable with itself, this test checks that computation of
	 * compositionsByLeftSubProperty and compositionsByRightSubProperty work
	 * correctly and don't fall into an infinite cycle
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testCyclicCompositions() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();

		ModifiableIndexedObjectProperty H = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/H")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		// S1 -> S2 -> S3
		ModifiableIndexedObjectProperty S3 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S3")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty S2 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S2")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { S3 }, false);
		ModifiableIndexedObjectProperty S1 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { S2 }, false);
		// P1 -> P2 -> P3
		ModifiableIndexedObjectProperty P3 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/P3")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty P2 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/P2")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { P3 }, false);
		ModifiableIndexedObjectProperty P1 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/P1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { P2 }, false);
		// S3 -> R, R -> S1, R -> P1
		ModifiableIndexedObjectProperty R = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R")),
						new ModifiableIndexedPropertyChain[] { S3 },
						new ModifiableIndexedObjectProperty[] { S1, P1 }, false);
		// R o R -> H
		ModifiableIndexedPropertyChain RR = IndexedObjectsCreator
				.createIndexedChain(R, R,
						new ModifiableIndexedObjectProperty[] { H });
		// create the factory and run the computation
		int maxThreads = Runtime.getRuntime().availableProcessors();
		PropertyHierarchyCompositionComputation computation = new PropertyHierarchyCompositionComputation(
				Arrays.asList(H, S3, S2, S1, P3, P2, P1, R, RR),
				new PropertyHierarchyCompositionComputationFactory(),
				new ComputationExecutor(maxThreads,
						"test-hierarchy-compositions"), maxThreads,
				new DummyProgressMonitor());

		computation.process();

		assertTrue(R.getSaturated().getCompositionsByLeftSubProperty().get(R)
				.contains(RR));
		assertTrue(R.getSaturated().getCompositionsByRightSubProperty().get(R)
				.contains(RR));
	}

	/**
	 * Test method for {@link ReflexivePropertyComputation}.
	 */
	@Test
	public void testReflexivity() {
		ElkObjectFactory factory = new ElkObjectFactoryImpl();
		List<ModifiableIndexedObjectProperty> toldReflexive = new ArrayList<ModifiableIndexedObjectProperty>();

		ModifiableIndexedObjectProperty t = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/T")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty r1 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, true);
		ModifiableIndexedObjectProperty r = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		// r1 o r1 -> r, thus r must be reflexive
		ModifiableIndexedPropertyChain r1r1 = IndexedObjectsCreator
				.createIndexedChain(r1, r1,
						new ModifiableIndexedObjectProperty[] { r });

		ModifiableIndexedPropertyChain rt = IndexedObjectsCreator
				.createIndexedChain(r, t,
						new ModifiableIndexedObjectProperty[] {});
		// r o t -> u
		ModifiableIndexedObjectProperty u = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/U")),
						new ModifiableIndexedPropertyChain[] { rt },
						new ModifiableIndexedObjectProperty[] {}, false);

		ModifiableIndexedObjectProperty h = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/H")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, true);
		// h -> h1, thus h1 must be reflexive
		ModifiableIndexedObjectProperty h1 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/H1")),
						new ModifiableIndexedPropertyChain[] { h },
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty s = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		// r o h1 -> s, both are implicitly reflexive, thus s must be reflexive
		ModifiableIndexedPropertyChain rh1 = IndexedObjectsCreator
				.createIndexedChain(r, h1,
						new ModifiableIndexedObjectProperty[] { s });
		// finally, add some loops
		ModifiableIndexedObjectProperty v1 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/V1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty v2 = IndexedObjectsCreator
				.createIndexedObjectProperty(
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/V2")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedPropertyChain v1v1 = IndexedObjectsCreator
				.createIndexedChain(v1, v1,
						new ModifiableIndexedObjectProperty[] { v2 });
		ModifiableIndexedPropertyChain v2v2 = IndexedObjectsCreator
				.createIndexedChain(v2, v2,
						new ModifiableIndexedObjectProperty[] { v1 });

		toldReflexive.addAll(Arrays
				.asList(new ModifiableIndexedObjectProperty[] { r1, h }));

		final List<ModifiableIndexedPropertyChain> chains = Arrays
				.asList(new ModifiableIndexedPropertyChain[] { t, r1, r, r1r1,
						rt, u, h, h1, s, rh1, v1, v2, v1v1, v2v2 });
		final Set<IndexedPropertyChain> correctReflexive = new HashSet<IndexedPropertyChain>(
				Arrays.asList(new IndexedPropertyChain[] { r, h, h1, r1, r1r1,
						rh1, s }));
		// create the factory and run the computation
		int maxThreads = Runtime.getRuntime().availableProcessors();

		ReflexivePropertyComputation computation = new ReflexivePropertyComputation(
				toldReflexive, new ReflexivePropertyComputationFactory(),
				new ComputationExecutor(maxThreads, "test-reflexivity"),
				maxThreads, new DummyProgressMonitor());

		computation.process();

		for (IndexedPropertyChain chain : chains) {
			assertEquals(chain.toString(), correctReflexive.contains(chain),
					isReflexive(chain));
		}
	}

	private boolean isReflexive(IndexedPropertyChain chain) {
		return chain.accept(new IndexedPropertyChainVisitor<Boolean>() {

			@Override
			public Boolean visit(IndexedObjectProperty prop) {
				return prop.isToldReflexive()
						|| (prop.getSaturated().isDerivedReflexive());
			}

			@Override
			public Boolean visit(IndexedBinaryPropertyChain binaryChain) {
				return binaryChain.getSaturated().isDerivedReflexive();
			}
		});
	}

}
