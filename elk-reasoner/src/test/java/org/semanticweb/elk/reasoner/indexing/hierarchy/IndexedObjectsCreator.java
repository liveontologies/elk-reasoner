/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

/**
 * A utility class to create indexed objects for other tests
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedObjectsCreator {

	public static IndexedObjectProperty createIndexedObjectProperty(
			ElkObjectProperty prop, IndexedPropertyChain[] toldSubs,
			IndexedObjectProperty[] toldSupers, boolean reflexive) {

		IndexedObjectProperty property = new IndexedObjectProperty(prop);

		for (IndexedPropertyChain sub : toldSubs) {
			property.addToldSubObjectProperty(sub);
			sub.addToldSuperObjectProperty(property);
		}

		for (IndexedObjectProperty sup : toldSupers) {
			property.addToldSuperObjectProperty(sup);
			sup.addToldSubObjectProperty(property);
		}
		
		if (reflexive) {
			property.reflexiveAxiomOccurrenceNo = 1;
		}

		return property;
	}
	
	public static IndexedPropertyChain createIndexedChain(
			IndexedObjectProperty left, IndexedPropertyChain right,
			IndexedObjectProperty[] toldSupers) {

		IndexedPropertyChain chain = new IndexedBinaryPropertyChain(left, right);

		for (IndexedObjectProperty sup : toldSupers) {
			chain.addToldSuperObjectProperty(sup);
			sup.addToldSubObjectProperty(chain);
		}

		return chain;
	}	
}
