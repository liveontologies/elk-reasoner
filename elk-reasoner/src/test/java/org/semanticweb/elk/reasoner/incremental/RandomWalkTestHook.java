/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.List;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface RandomWalkTestHook {

	public void hook(Reasoner reasoner,
			final OnOffVector<ElkAxiom> changingAxioms,
			final List<ElkAxiom> staticAxioms) throws ElkException;
}
