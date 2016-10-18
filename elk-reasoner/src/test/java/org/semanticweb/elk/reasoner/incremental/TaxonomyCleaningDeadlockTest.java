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
package org.semanticweb.elk.reasoner.incremental;

import java.util.Random;

import org.junit.Test;
import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkPrefixImpl;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests whether there is a deadlock during taxonomy cleaning. This may be the
 * case when two super-nodes are sharing a number of sub-nodes.
 * 
 * @author Peter Skocovsky
 */
public class TaxonomyCleaningDeadlockTest {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TaxonomyCleaningDeadlockTest.class);

	private final int nIterations = 100;
	private final int nSuperNodes = 2;
	private final int nSubNodes = 100;
	private final double pSubsumes = 0.9;
	private final double pRemove = 1;

	private final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

	private final ElkPrefixImpl p = new ElkPrefixImpl(":",
			new ElkFullIri(TaxonomyCleaningDeadlockTest.class.getName()));

	private ElkClass createElkClass(String name) {
		return objectFactory.getClass(new ElkAbbreviatedIri(p, name));
	}

	@Test
	public void testBasicDeletion() throws ElkException {

		final Random random = new Random(RandomSeedProvider.VALUE);

		for (int i = 0; i < nIterations; i++) {

			final TestChangesLoader loader = new TestChangesLoader();

			final Reasoner reasoner = TestReasonerUtils
					.createTestReasoner(loader, new LoggingStageExecutor());
			reasoner.setAllowIncrementalMode(false);

			final Multimap<Integer, ElkSubClassOfAxiom> axiomsPerSuperClass = new HashSetMultimap<Integer, ElkSubClassOfAxiom>();

			// Add the axioms

			for (int superIndex = 0; superIndex < nSuperNodes; superIndex++) {
				for (int subIndex = 0; subIndex < nSubNodes; subIndex++) {
					if (random.nextDouble() < pSubsumes) {
						final ElkSubClassOfAxiom axiom = objectFactory
								.getSubClassOfAxiom(
										createElkClass("Super" + superIndex),
										createElkClass("Sub" + subIndex));
						loader.add(axiom);
						axiomsPerSuperClass.add(superIndex, axiom);
					}
				}
			}

			LOGGER_.trace(
					"********************* Initial taxonomy computation *********************");

			reasoner.getTaxonomy();

			// Remove some axioms

			reasoner.setAllowIncrementalMode(true);
			TestChangesLoader changeLoader = new TestChangesLoader();

			for (int superIndex = 0; superIndex < nSuperNodes; superIndex++) {
				if (random.nextDouble() < pRemove) {
					for (final ElkSubClassOfAxiom axiom : axiomsPerSuperClass
							.get(superIndex)) {
						changeLoader.remove(axiom);
					}
				}
			}

			reasoner.registerAxiomLoader(
					new TestAxiomLoaderFactory(changeLoader));

			LOGGER_.trace(
					"********** Taxonomy re-computation after incremental change ************");

			reasoner.getTaxonomy();

		}

	}

}
