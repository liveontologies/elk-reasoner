/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * 
 */
package org.semanticweb.elk.reasoner;

/**
 * A simple bean to hold name and value for a configuration parameter
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public final class ConfigurationParameter<V> {
	
	private final String name;
	private final V value;
	
	ConfigurationParameter(String name, V value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public V getValue() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}
}
