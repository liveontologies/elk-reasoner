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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A key for the {@link Context}. Two {@link Root}s are equal if they contain
 * the same {@link IndexedClassExpression}s. For each {@link Root} there should
 * exist at most one {@link Context} with this {@link Root} modulo equality.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class Root extends ArrayHashSet<IndexedClassExpression> {

	public Root(IndexedClassExpression... elements) {
		super(elements.length);
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}
	}

	// TODO: cache hash code for frequent use

}
