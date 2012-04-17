/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.parsing;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * Used to test parsers
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ElkTestAxiomProcessor implements ElkAxiomProcessor {

	private Map<Class<?>, Set<ElkAxiom>> axiomMap = new HashMap<Class<?>, Set<ElkAxiom>>();
	
	@Override
	public void process(ElkAxiom elkAxiom) {
		if (elkAxiom == null) {
			return;
		}
		//assert elkAxiom != null : "The parser failed to parse an axiom";
		//System.out.println(OwlFunctionalStylePrinter.toString(elkAxiom));
		
		Set<ElkAxiom> axioms = axiomMap.get(elkAxiom.getClass());
		
		axioms = axioms == null ? new HashSet<ElkAxiom>() : axioms;
		axioms.add(elkAxiom);
		axiomMap.put(elkAxiom.getClass(), axioms);
	}

	int getAxiomCountForType(Class<?> axiomClass) {
		Set<ElkAxiom> axioms = axiomMap.get(axiomClass);
		
		return axioms == null ? 0 : axioms.size();
	}
	
	Set<ElkAxiom> getAxiomsForType(Class<?> axiomClass) {
		Set<ElkAxiom> axioms = axiomMap.get(axiomClass);
		
		return axioms == null ? Collections.<ElkAxiom>emptySet() : axioms;
	}
	
	Set<Map.Entry<Class<?>, Set<ElkAxiom>>> getAxiomMapEntries() {
		return axiomMap.entrySet();
	}
	
	long getTotalAxiomCount() {
		long count = 0;
		
		for (Map.Entry<Class<?>, Set<ElkAxiom>> actualEntry : getAxiomMapEntries()) {
			count += actualEntry.getValue().size();
		}
		
		return count;
	}
}
