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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.util.collections.ArrayHashSet;
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
	
	private Set<IndexedObjectProperty> propsWithTransitiveSubProps_ = null;
	/**
	 * Computes the transitive closure of the told object property hierarchy.
	 * 
	 * TODO could be optimized
	 * 
	 * @param index
	 */
	public Collection<IndexedObjectProperty> compute(OntologyIndex index) {		
		for (IndexedObjectProperty nextProperty : index.getIndexedObjectProperties()) {
			addToSuperProperties(nextProperty);
		}
		
		return propsWithTransitiveSubProps_ == null ? Collections.<IndexedObjectProperty>emptySet() : propsWithTransitiveSubProps_;
	}
	
	private void addPropertyWithTransitiveSubProperty(IndexedObjectProperty property) {
		if (propsWithTransitiveSubProps_ == null) {
			propsWithTransitiveSubProps_ = new ArrayHashSet<IndexedObjectProperty>(4);
		}
		
		propsWithTransitiveSubProps_.add(property);
	}
	
	public Collection<IndexedObjectProperty> getPropertiesWithTransitiveSubProperties() {
		return propsWithTransitiveSubProps_;
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
				//for transitivity encoding
				addPropertyWithTransitiveSubProperty(superProperty);
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
