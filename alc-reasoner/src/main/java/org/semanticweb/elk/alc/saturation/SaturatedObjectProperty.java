/**
 * 
 */
package org.semanticweb.elk.alc.saturation;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturatedObjectProperty {

	/**
	 * the {@code IndexedObjectProperty}s which are subsumed by this property.
	 */
	private Set<IndexedObjectProperty> derivedSubProperties;
	
	public Set<IndexedObjectProperty> getSubProperties() {
		return derivedSubProperties != null ? derivedSubProperties : Collections.<IndexedObjectProperty>emptySet();
	}

	public boolean addSubProperty(IndexedObjectProperty subProperty) {
		if (derivedSubProperties == null) {
			derivedSubProperties = new ArrayHashSet<IndexedObjectProperty>(2);
		}
		
		return derivedSubProperties.add(subProperty);
	}
	

	
}
