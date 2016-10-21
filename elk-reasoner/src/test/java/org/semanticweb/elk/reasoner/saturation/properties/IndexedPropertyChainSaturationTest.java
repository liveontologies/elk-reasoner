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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.reasoner.DummyProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.classes.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.classes.IndexedObjectsCreator;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.properties.VerifySymmetricPropertySaturation.AsymmetricCompositionHook;
import org.semanticweb.elk.reasoner.stages.PropertyHierarchyCompositionState;
import org.semanticweb.elk.reasoner.tracing.TracingInferenceProducer;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.DummyInterruptMonitor;

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
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ModifiableOntologyIndex index = new DirectIndex(factory);

		ModifiableIndexedObjectProperty R1 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty R2 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/R2")),
						new ModifiableIndexedPropertyChain[] { R1 },
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty R3 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
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
		ElkObject.Factory factory = new ElkObjectEntityRecyclingFactory();
		ModifiableOntologyIndex index = new DirectIndex(factory);

		ModifiableIndexedObjectProperty H = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/H")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		// S1 -> S2 -> S3
		ModifiableIndexedObjectProperty S3 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S3")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty S2 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S2")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { S3 }, false);
		ModifiableIndexedObjectProperty S1 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/S1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { S2 }, false);
		// P1 -> P2 -> P3
		ModifiableIndexedObjectProperty P3 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/P3")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] {}, false);
		ModifiableIndexedObjectProperty P2 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/P2")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { P3 }, false);
		ModifiableIndexedObjectProperty P1 = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
						factory.getObjectProperty(new ElkFullIri(
								"http://test.com/P1")),
						new ModifiableIndexedPropertyChain[] {},
						new ModifiableIndexedObjectProperty[] { P2 }, false);
		// S3 -> R, R -> S1, R -> P1
		ModifiableIndexedObjectProperty R = IndexedObjectsCreator
				.createIndexedObjectProperty(index,
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
				new PropertyHierarchyCompositionComputationFactory(
						DummyInterruptMonitor.INSTANCE,
						TracingInferenceProducer.DUMMY,
						PropertyHierarchyCompositionState.Dispatcher.DUMMY),
				new ComputationExecutor(maxThreads,
						"test-hierarchy-compositions"), maxThreads,
				new DummyProgressMonitor());

		computation.process();

		assertTrue(R.getSaturated().getNonRedundantCompositionsByLeftSubProperty().get(R)
				.contains(RR));
		assertTrue(R.getSaturated().getNonRedundantCompositionsByRightSubProperty().get(R)
				.contains(RR));
	}

}
