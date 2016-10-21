/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Removes all axioms from the reasoner
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CleanIndexHook implements RandomWalkTestHook {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CleanIndexHook.class);

	@SuppressWarnings("unchecked")
	@Override
	public void apply(Reasoner reasoner, OnOffVector<ElkAxiom> changingAxioms,
			List<ElkAxiom> staticAxioms) throws ElkException {
		clearIndexTest(reasoner,
				Operations.concat(changingAxioms.getOnElements(), staticAxioms));
	}

	public static void clearIndexTest(Reasoner reasoner,
			Iterable<ElkAxiom> axioms) throws ElkException {
		int size = getIndexSize(reasoner);

		LOGGER_.debug("initial size is {}", size);

		// remove everything from the index
		TestChangesLoader loader = new TestChangesLoader();

		for (ElkAxiom axiom : axioms) {
			if (axiom instanceof ElkClassAxiom
					|| axiom instanceof ElkObjectPropertyDomainAxiom) {
				loader.remove(axiom);
			}
		}

		LOGGER_.debug("Cleaning the index...");

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(loader));
		reasoner.setAllowIncrementalMode(false);
		// trigger removal
		reasoner.getTaxonomyQuietly();
		// check that the index is indeed empty
		int cnt = getIndexSize(reasoner);

		if (cnt > 2) {
			LOGGER_.error("index must be empty but its size is " + cnt);
		}
		// add stuff back
		// loader.clear();

		for (ElkAxiom axiom : axioms) {
			if (axiom instanceof ElkClassAxiom
					|| axiom instanceof ElkObjectPropertyDomainAxiom) {
				loader.add(axiom);
			}
		}

		reasoner.registerAxiomLoader(new TestAxiomLoaderFactory(loader));
		reasoner.getTaxonomyQuietly();

		cnt = getIndexSize(reasoner);

		if (cnt != size) {
			LOGGER_.error("index size must be " + size + " but is " + cnt);
		}

		reasoner.setAllowIncrementalMode(true);
	}

	private static int getIndexSize(Reasoner reasoner) {
		int cnt = 0;

		for (IndexedClassExpression ice : reasoner.getIndexedClassExpressions()) {
			cnt++;
			LOGGER_.trace(ice + ": indexed");
		}
		if (reasoner.getIndexedClassExpressions().size() != cnt)
			LOGGER_.error("Index size mismatch: "
					+ reasoner.getIndexedClassExpressions().size() + "!=" + cnt);

		return cnt;
	}

}
