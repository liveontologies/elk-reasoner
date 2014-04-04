/**
 * 
 */
package org.semanticweb.elk.alc.saturation;
/*
 * #%L
 * ALC Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
