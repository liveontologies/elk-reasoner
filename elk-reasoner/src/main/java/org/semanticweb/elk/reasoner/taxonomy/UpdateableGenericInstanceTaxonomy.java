package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTypeNode;

public interface UpdateableGenericInstanceTaxonomy<K, M, KI, I, 
			TN extends GenericTypeNode<K, M, KI, I, TN, IN>, 
			IN extends GenericInstanceNode<K, M, KI, I, TN, IN>>
		extends 
		GenericInstanceTaxonomy<K, M, KI, I, TN, IN>,
		UpdateableInstanceTaxonomy<K, M, KI, I>,
		UpdateableGenericTaxonomy<K, M, TN> {

}
