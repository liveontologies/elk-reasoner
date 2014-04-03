/**
 * 
 */
package org.semanticweb.elk.alc.saturation;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Contains derived sub- and super-properties to be used during saturation.
 * Stores transitive properties separately.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturatedObjectProperty {

	/**
	 * the {@code IndexedObjectProperty}s which are subsumed by this property.
	 */
	private Set<IndexedObjectProperty> derivedSubProperties;
	/**
	 * the transitive {@code IndexedObjectProperty}s which are subsumed by this property.
	 */
	private Set<IndexedObjectProperty> derivedTransitiveSubProperties;
	/**
	 * the {@code IndexedObjectProperty}s which subsume this property.
	 */
	private Set<IndexedObjectProperty> derivedSuperProperties;
	/**
	 * the transitive {@code IndexedObjectProperty}s which subsume this property.
	 */
	private Set<IndexedObjectProperty> derivedTransitiveSuperProperties;
	
	public Set<IndexedObjectProperty> getSubProperties() {
		return derivedSubProperties != null ? derivedSubProperties : Collections.<IndexedObjectProperty>emptySet();
	}
	
	public Set<IndexedObjectProperty> getSuperProperties() {
		return derivedSuperProperties != null ? derivedSuperProperties : Collections.<IndexedObjectProperty>emptySet();
	}

	public boolean addSubProperty(IndexedObjectProperty subProperty) {
		if (derivedSubProperties == null) {
			derivedSubProperties = new ArrayHashSet<IndexedObjectProperty>(2);
		}
		
		return derivedSubProperties.add(subProperty);
	}
	
	public boolean addSuperProperty(IndexedObjectProperty subProperty) {
		if (derivedSuperProperties == null) {
			derivedSuperProperties = new ArrayHashSet<IndexedObjectProperty>(2);
		}
		
		return derivedSuperProperties.add(subProperty);
	}
	
	public boolean addTransitiveSubProperty(IndexedObjectProperty subProperty) {
		addSubProperty(subProperty);
		
		if (derivedTransitiveSubProperties == null) {
			derivedTransitiveSubProperties = new ArrayHashSet<IndexedObjectProperty>(2);
		}
		
		return derivedTransitiveSubProperties.add(subProperty);
	}
	
	public boolean addTransitiveSuperProperty(IndexedObjectProperty superProperty) {
		addSuperProperty(superProperty);
		
		if (derivedTransitiveSuperProperties == null) {
			derivedTransitiveSuperProperties = new ArrayHashSet<IndexedObjectProperty>(2);
		}
		
		return derivedTransitiveSuperProperties.add(superProperty);
	}
	
	public Set<IndexedObjectProperty> getTransitiveSubProperties() {
		return derivedTransitiveSubProperties != null ? derivedTransitiveSubProperties : Collections.<IndexedObjectProperty>emptySet();
	}
	
	public Set<IndexedObjectProperty> getTransitiveSuperProperties() {
		return derivedTransitiveSuperProperties != null ? derivedTransitiveSuperProperties : Collections.<IndexedObjectProperty>emptySet();
	}

	
}
