/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.markers;



/**
 * An interface for objects marked with a set of Markers. The intended use it 
 * have the parameter class T also implement Marked<T> and the naked objects of
 * type T then represent definite (unmarked) objects. Elements of Marked<T> are
 * unmutable, that is their key and markers will not change after creation. 
 * 
 * 
 * @author Frantisek Simancik
 *
 */
public interface Marked<T> extends Entry<T> {
	/**
	 * 
	 * @return unmodifiable non-empty set of markers 
	 */
	Markers getMarkers();
}