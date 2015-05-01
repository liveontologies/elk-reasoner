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
package org.semanticweb.elk.owl.implementation;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Implementation for {@link ElkObject}s that maintain a list of other
 * ElkObjects.
 * 
 * @author Markus Kroetzsch
 * @param <O>
 *            the type of {@link ElkObject}s in the list
 * 
 */
public abstract class ElkObjectListObject<O extends ElkObject> extends
		ElkObjectImpl {

	private final List<? extends O> elkObjects_;

	ElkObjectListObject(List<? extends O> elkObjects) {
		this.elkObjects_ = elkObjects;
	}

	public List<? extends O> getObjects() {
		return elkObjects_;
	}

	public static <O> List<? extends O> varArgsToList(O firstObject,
			O secondObject, O... otherObjects) {
		List<O> objects = new ArrayList<O>(2 + otherObjects.length);
		objects.add(firstObject);
		objects.add(secondObject);
		for (int i = 0; i < otherObjects.length; ++i) {
			objects.add(otherObjects[i]);
		}
		return objects;
	}

	public static <O> List<? extends O> varArgsToList(O firstObject,
			O... otherObjects) {
		List<O> objects = new ArrayList<O>(1 + otherObjects.length);
		objects.add(firstObject);
		for (int i = 0; i < otherObjects.length; ++i) {
			objects.add(otherObjects[i]);
		}
		return objects;
	}

}
