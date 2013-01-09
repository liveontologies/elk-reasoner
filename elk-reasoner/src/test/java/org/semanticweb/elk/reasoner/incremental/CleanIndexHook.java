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

/**
 * Removes all axioms from the reasoner
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class CleanIndexHook implements RandomWalkTestHook {

	private static final Logger LOGGER_ = Logger
			.getLogger(CleanIndexHook.class);	
	
	@Override
	public void hook(Reasoner reasoner, OnOffVector<ElkAxiom> changingAxioms,
			List<ElkAxiom> staticAxioms) throws ElkException {
		int size = getIndexSize(reasoner);
		
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("initial size is " + size);
		}
		
		// remove everything from the index
		TestChangesLoader loader = new TestChangesLoader();
		
		for (int i = 0; i < changingAxioms.size(); i++) {
			if (changingAxioms.isOn(i)) {
				loader.remove(changingAxioms.get(i));
			}
		}
		
		for (ElkAxiom staticAxiom : staticAxioms) {
			loader.remove(staticAxiom);
		}
		
		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Cleaning the index...");
		}
		
		reasoner.registerOntologyChangesLoader(loader);
		reasoner.setIncrementalMode(false);
		//trigger removal
		reasoner.getTaxonomyQuietly();
		//check that the index is indeed empty
		int cnt = getIndexSize(reasoner);
		
		if (cnt > 2) {
			throw new RuntimeException("index must be empty but its size is " + cnt);
		}
		//add stuff back
		loader.clear();
		
		for (int i = 0; i < changingAxioms.size(); i++) {
			if (changingAxioms.isOn(i)) {
				loader.add(changingAxioms.get(i));
			}
		}
		
		for (ElkAxiom staticAxiom : staticAxioms) {
			loader.add(staticAxiom);
		}
		
		reasoner.registerOntologyChangesLoader(loader);
		reasoner.getTaxonomyQuietly();
		
		cnt = getIndexSize(reasoner);
		
		if (cnt != size) {
			throw new RuntimeException("index size must be " + size + " but is " + cnt);
		}
		
		reasoner.setIncrementalMode(true);
	}
	
	private int getIndexSize(Reasoner reasoner) {
		int cnt = 0;
		
		for (IndexedClassExpression ice : reasoner.getIndexedClassExpressions()) {
			cnt++;
			LOGGER_.debug("indexed CE: " + ice + " " + ice.printOccurrenceNumbers());
		}
		
		return cnt;
	}

}
