/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * TODO doc
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface NonIncrementalChangeListener<T> {

	public void notify(T axiom);
	
	public static NonIncrementalChangeListener<ElkAxiom> DUMMY = new NonIncrementalChangeListener<ElkAxiom>() {

		@Override
		public void notify(ElkAxiom axiom) {
		}		
	};
}
