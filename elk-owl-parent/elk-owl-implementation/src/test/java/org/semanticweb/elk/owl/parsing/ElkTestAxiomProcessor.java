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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * Used to test parsers
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */
public class ElkTestAxiomProcessor implements ElkAxiomProcessor {

	private Map<Class<?>, Set<ElkAxiom>> axiomMap = new HashMap<Class<?>, Set<ElkAxiom>>();

	@Override
	public void visit(ElkAxiom elkAxiom) {
		if (elkAxiom == null) {
			return;
		}

		assert elkAxiom != null : "The parser failed to parse an axiom";

		Class<?> axiomType = getElkAxiomType(elkAxiom.getClass());

		assert axiomType != null : "Couldn't determine a suitable Elk OWL interface for the axiom: "
				+ elkAxiom;

		Set<ElkAxiom> axioms = axiomMap.get(axiomType);

		axioms = axioms == null ? new HashSet<ElkAxiom>() : axioms;
		axioms.add(elkAxiom);
		axiomMap.put(axiomType, axioms);
	}

	/*
	 * Returns the most specific interface in the org.semanticweb.elk.interfaces
	 * package
	 */
	@SuppressWarnings("static-method")
	private Class<?> getElkAxiomType(Class<? extends ElkAxiom> elkAxiomClass) {
		Package elkOwlPackage = ElkAxiom.class.getPackage();

		if (elkAxiomClass.isInterface()
				&& elkAxiomClass.getPackage().equals(elkOwlPackage)) {
			return elkAxiomClass;
		}

		for (Class<?> interface_ : elkAxiomClass.getInterfaces()) {
			if (interface_.getPackage().equals(elkOwlPackage)) {
				return interface_;
			}
		}

		return null;
	}

	public int getAxiomCountForType(Class<? extends ElkAxiom> axiomClass) {
		Set<ElkAxiom> axioms = axiomMap.get(getElkAxiomType(axiomClass));

		return axioms == null ? 0 : axioms.size();
	}

	public Set<ElkAxiom> getAxiomsForType(Class<? extends ElkAxiom> axiomClass) {
		Set<ElkAxiom> axioms = axiomMap.get(getElkAxiomType(axiomClass));

		return axioms == null ? Collections.<ElkAxiom> emptySet() : axioms;
	}

	public Set<Map.Entry<Class<?>, Set<ElkAxiom>>> getAxiomMapEntries() {
		return axiomMap.entrySet();
	}

	public Set<ElkAxiom> getAllAxioms() {
		Set<ElkAxiom> axioms = new HashSet<ElkAxiom>();

		for (Map.Entry<Class<?>, Set<ElkAxiom>> entry : getAxiomMapEntries()) {
			axioms.addAll(entry.getValue());
		}

		return axioms;
	}

	long getTotalAxiomCount() {
		return getAllAxioms().size();
	}
}