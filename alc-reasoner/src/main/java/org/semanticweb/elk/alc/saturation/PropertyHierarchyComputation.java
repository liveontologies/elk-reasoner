/**
 * 
 */
package org.semanticweb.elk.alc.saturation;

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PropertyHierarchyComputation {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(PropertyHierarchyComputation.class);
	//TODO make a local var?
	private final Queue<IndexedObjectProperty> toDo_ = new LinkedList<IndexedObjectProperty>();
	
	/**
	 * Computes the transitive closure of the told object property hierarchy.
	 * 
	 * TODO could be optimized
	 * 
	 * @param index
	 */
	public void compute(OntologyIndex index) {
		for (IndexedObjectProperty nextProperty : index.getIndexedObjectProperties()) {
			addToSuperProperties(nextProperty);
		}
	}

	private void addToSuperProperties(IndexedObjectProperty property) {
		LOGGER_.trace("Adding {} to all its inferred super-properties", property);
		// add this property to all its super-properties
		toDo_.add(property);
				
		for (;;) {
			IndexedObjectProperty superProperty = toDo_.poll();
			
			if (superProperty == null) {
				break;
			}

			if (property.isTransitive()) {
				superProperty.getSaturatedProperty().addTransitiveSubProperty(property);
			}
			else {
				superProperty.getSaturatedProperty().addSubProperty(property);
			}

			if (superProperty.isTransitive()) {
				property.getSaturatedProperty().addTransitiveSuperProperty(superProperty);
			}
			else {
				property.getSaturatedProperty().addSuperProperty(superProperty);
			}
			
			for (IndexedObjectProperty toldSuper : superProperty.getToldSuperProperties()) {
				toDo_.add(toldSuper);
			}
		}
		// don't care too much about memory at this point
		property.getSaturatedProperty();
	}

}
