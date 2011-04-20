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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Index<Key, Value> {
	protected HashMap<Key, List<Value>> map = new HashMap<Key, List<Value>> ();

	public List<Value> get(Key key) {
		return map.get(key);
		/*		
		List<Value> list = map.get(key);
		if (list == null)
			return new ArrayList<Value> ();
		return list;
		 */
	}
	
	public void put(Key key, Value value) {
		List<Value> list = map.get(key);
		if (list == null) {
			list = new ArrayList<Value> (1);
			map.put(key, list);
		}
		list.add(value);
	}
	
	public Set<Map.Entry<Key, List<Value>>> entrySet() {
		return map.entrySet();
	}
}