package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;

/**
 * Represents a conclusion that a chain is a sub-property chain of another chain
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public interface SubPropertyChain<R extends IndexedPropertyChain, S extends IndexedPropertyChain>
		extends ObjectPropertyConclusion {

	public R getSubPropertyChain();

	public S getSuperPropertyChain();

}
