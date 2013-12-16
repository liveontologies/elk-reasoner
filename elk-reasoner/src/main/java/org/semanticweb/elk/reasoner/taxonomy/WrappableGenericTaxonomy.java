package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Map;

import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericNonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.nodes.GenericTaxonomyNode;

public interface WrappableGenericTaxonomy<K, M,
			N extends GenericTaxonomyNode<K, M, N>,
			NB extends GenericNonBottomTaxonomyNode<K, M, N, NB>,
			BN extends GenericBottomTaxonomyNode<K, M, N>>
		extends 
		UpdateableGenericTaxonomy<K, M, N> {
	
	NB getCreateNonBottomNode(Map<K, M> members);
	
	NB getCreateTopNode();
	
	BN getCreateBottomNode();

	
	interface Min<K, M> 
	extends
	UpdateableGenericTaxonomy.Min<K, M>,
	WrappableGenericTaxonomy<K, M, 
				GenericTaxonomyNode.Min<K, M>, 
				GenericNonBottomTaxonomyNode.Min<K, M>,
				GenericBottomTaxonomyNode.Min<K, M>> {
		
	}
	
}
