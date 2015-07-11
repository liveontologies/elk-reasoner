package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;

public interface ReflexivePropertyChain<P extends IndexedPropertyChain> extends
		ObjectPropertyConclusion {

	public P getPropertyChain();

}
