/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
/*
 * #%L
 * ELK Reasoner
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;

/**
 * An extension of {@link IndexedObjectProperty} with an explicit binding to
 * axioms for told sub-property chains.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IndexedObjectPropertyWithBinding extends IndexedObjectProperty {

	private List<ElkObjectPropertyAxiom> chainAxioms_ = null;
	
	private ElkReflexiveObjectPropertyAxiom reflexivityAxiom_;

	protected IndexedObjectPropertyWithBinding(
			ElkObjectProperty elkObjectProperty) {
		super(elkObjectProperty);
	}

	@Override
	protected void addToldSubPropertyChain(
			IndexedPropertyChain subObjectProperty) {
		throw new IllegalStateException("SubObjectPropertyOf axiom has to be provided");
	}

	@Override
	protected boolean removeToldSubObjectProperty(
			IndexedPropertyChain subChain) {
		if (toldSubProperties == null) {
			return false;
		}
		
		for (int i = 0; i < toldSubProperties.size(); i++) {
			if (subChain == toldSubProperties.get(i)) {
				toldSubProperties.remove(i);
				chainAxioms_.remove(i);
				
				if (toldSubProperties.isEmpty()) {
					toldSubProperties = null;
				}
				
				return true;
			}
		}

		return false;
	}

	protected void addToldSubPropertyChain(
			IndexedPropertyChain subObjectProperty,
			ElkObjectPropertyAxiom axiom) {
		super.addToldSubPropertyChain(subObjectProperty);

		if (chainAxioms_ == null) {
			chainAxioms_ = new ArrayList<ElkObjectPropertyAxiom>(1);
		}

		chainAxioms_.add(axiom);
	}

	public ElkObjectPropertyAxiom getSubChainAxiom(
			IndexedPropertyChain subChain) {
		if (chainAxioms_ == null) {
			// shouldn't happen
			return null;
		}

		for (int i = 0; i < toldSubProperties.size(); i++) {
			if (subChain == toldSubProperties.get(i)) {
				return chainAxioms_.get(i);
			}
		}

		return null;
	}

	public ElkReflexiveObjectPropertyAxiom getReflexivityAxiom() {
		return reflexivityAxiom_;
	}
	
	public void setAxiom(ElkReflexiveObjectPropertyAxiom axiom) {
		reflexivityAxiom_ = axiom;
	}
}
