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
package org.semanticweb.elk.reasoner;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Frantisek Simancik
 *
 * @param <T>
 */
public class EquivalenceClass<T> {
	protected List<T> members;
	protected List<EquivalenceClass<T>> directSubClasses;
	protected List<EquivalenceClass<T>> directSuperClasses;
	
	public EquivalenceClass() {
		members = new ArrayList<T> ();
		directSubClasses = new ArrayList<EquivalenceClass<T>> ();
		directSuperClasses = new ArrayList<EquivalenceClass<T>> ();
	}
	
	public T getCanonicalMember() {
		return members.get(0);
	}
	
	public List<T> getMembers() {
		return members;
	}
	
	public List<EquivalenceClass<T>> getDirectSuperClasses() {
		return directSuperClasses;
	}
	
	public List<EquivalenceClass<T>> getDirectSubClasses() {
		return directSubClasses;
	}
}