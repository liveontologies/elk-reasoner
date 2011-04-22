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

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.elk.util.ArraySet;
import org.semanticweb.elk.util.Index;
import org.semanticweb.elk.util.HashIndex;


/**
 * @author Frantisek Simancik
 *
 */
class Context implements DerivableVisitor<Boolean> {
	Set<Concept> derivedConcepts = new ArraySet<Concept> ();
	Set<Existential> derivedExistentials = new ArraySet<Existential> ();
	
	ArrayList<Derivable> queue = new ArrayList<Derivable> ();
	Index<Role, Context> linksToParents = new HashIndex<Role, Context> ();
	
	boolean saturated = true;

	public Boolean visit(Concept concept) {
		return derivedConcepts.add(concept);
	}

	public Boolean visit(Existential existential) {
		return derivedExistentials.add(existential);
	}
}