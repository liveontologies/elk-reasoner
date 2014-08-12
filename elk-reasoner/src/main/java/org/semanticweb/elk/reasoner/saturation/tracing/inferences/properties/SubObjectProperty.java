/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;

/**
 * Just a convenience specialization of {@link SubPropertyChain}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubObjectProperty extends SubPropertyChain<IndexedObjectProperty, IndexedObjectProperty> {

	public SubObjectProperty(IndexedObjectProperty chain, IndexedObjectProperty sup) {
		super(chain, sup);
	}
}
