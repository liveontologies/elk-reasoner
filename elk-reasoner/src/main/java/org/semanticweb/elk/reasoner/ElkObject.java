/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.reasoner;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Yevgeny Kazakov
 * 
 */
public abstract class ElkObject {

	public abstract <O> O accept(ElkObjectVisitor<O> visitor);

	public abstract int hashCode();

	public abstract boolean equals(Object obj);

	private static final Map<ElkObject, WeakReference<ElkObject>> elkObjectCache_ 
	   = new WeakHashMap<ElkObject, WeakReference<ElkObject>>();

	protected static ElkObject intern(ElkObject elkObject) {
		WeakReference<ElkObject> reference = elkObjectCache_.get(elkObject);
		if (reference != null) {
			ElkObject cashedElkObject = reference.get();
			if (cashedElkObject != null)
				return cashedElkObject;
		}
		elkObjectCache_.put(elkObject, new WeakReference<ElkObject>(elkObject));
		return elkObject;
	}

}
