/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Removes all axioms from the reasoner
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CleanIndexHook implements RandomWalkTestHook {

	private static final Logger LOGGER_ = Logger
			.getLogger(CleanIndexHook.class);

	@Override
	public void hook(Reasoner reasoner, OnOffVector<ElkAxiom> changingAxioms,
			List<ElkAxiom> staticAxioms) throws ElkException {
		hook(reasoner,
				Operations.concat(changingAxioms.getOnElements(), staticAxioms));
	}

	public void hook(Reasoner reasoner, Iterable<ElkAxiom> axioms) {
		int size = getIndexSize(reasoner);

		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("initial size is " + size);
		}

		// remove everything from the index
		TestChangesLoader loader = new TestChangesLoader();

		for (ElkAxiom axiom : axioms) {
			loader.remove(axiom);
		}

		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Cleaning the index...");
		}

		reasoner.registerOntologyChangesLoader(loader);
		reasoner.setIncrementalMode(false);
		// trigger removal
		reasoner.getTaxonomyQuietly();
		// check that the index is indeed empty
		int cnt = getIndexSize(reasoner);

		if (cnt > 2) {
			LOGGER_.error("index must be empty but its size is " + cnt);
		}
		// add stuff back
		loader.clear();

		for (ElkAxiom axiom : axioms) {
			loader.add(axiom);
		}

		reasoner.registerOntologyChangesLoader(loader);
		reasoner.getTaxonomyQuietly();

		cnt = getIndexSize(reasoner);

		if (cnt != size) {
			LOGGER_.error("index size must be " + size + " but is " + cnt);
		}

		reasoner.setIncrementalMode(true);
	}

	private int getIndexSize(Reasoner reasoner) {
		int cnt = 0;

		for (IndexedClassExpression ice : reasoner.getIndexedClassExpressions()) {
			cnt++;
			LOGGER_.trace("indexed CE: " + ice + " "
					+ ice.printOccurrenceNumbers());
		}
		if (reasoner.getIndexedClassExpressions().size() != cnt)
			LOGGER_.error("Index size mismatch: "
					+ reasoner.getIndexedClassExpressions().size() + "!=" + cnt);

		return cnt;
	}

}
