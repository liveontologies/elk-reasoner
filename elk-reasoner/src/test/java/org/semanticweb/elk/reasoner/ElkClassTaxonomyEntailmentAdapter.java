package org.semanticweb.elk.reasoner;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.testing.DiffableOutput.Listener;

public class ElkClassTaxonomyEntailmentAdapter
		implements TaxonomyEntailment.Listener<ElkClass> {

	final static ElkAxiom.Factory ELK_FACTORY = new ElkObjectBaseFactory();

	private final Listener<ElkAxiom> listener_;

	public ElkClassTaxonomyEntailmentAdapter(Listener<ElkAxiom> listener) {
		this.listener_ = listener;
	}

	Listener<ElkAxiom> getListener() {
		return listener_;
	}

	@Override
	public void reportMissingSubsumption(ElkClass sub, ElkClass sup) {
		listener_.missing(ELK_FACTORY.getSubClassOfAxiom(sub, sup));
	}

	@Override
	public void reportMissingEquivalence(ElkClass first, ElkClass second) {
		listener_.missing(ELK_FACTORY.getEquivalentClassesAxiom(first, second));
	}

	@Override
	public void reportMissingEntity(ElkClass entity) {
		listener_.missing(ELK_FACTORY.getDeclarationAxiom(entity));
	}

}
