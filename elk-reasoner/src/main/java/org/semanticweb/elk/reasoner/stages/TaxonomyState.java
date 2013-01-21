/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;

/**
 * Stores information about the state of the taxonomy
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TaxonomyState<T extends ElkObject> {

	UpdateableTaxonomy<T> taxonomy = null;
	
	Queue<T> modifiedNodeObjects = new ConcurrentLinkedQueue<T>();
	
}
