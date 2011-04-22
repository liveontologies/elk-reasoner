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
package org.semanticweb.elk.util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * Implementation of Index backed by a HashMap
 * 
 * @author Frantisek Simancik
 *
 * @param <Key>
 * @param <Value>
 */

public class HashIndex<Key, Value>  extends HashMap<Key, List<Value>> implements Index<Key, Value> {

	private static final long serialVersionUID = -3456364969567004248L;

	public void add(Key key, Value value) {
		List<Value> list = get(key);
		if (list == null) {
			list = new ArrayList<Value> (1);
			put(key, list);
		}
		list.add(value);
	}
}