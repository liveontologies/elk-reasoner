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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.testing.DiffableOutput.Listener;

public class ElkInstanceTaxonomyEntailmentAdapter
		extends ElkClassTaxonomyEntailmentAdapter implements
		InstanceTaxonomyEntailment.Listener<ElkClass, ElkNamedIndividual> {

	public ElkInstanceTaxonomyEntailmentAdapter(Listener<ElkAxiom> listener) {
		super(listener);
	}

	@Override
	public void reportMissingInstance(ElkNamedIndividual instance) {
		getListener().missing(ELK_FACTORY.getDeclarationAxiom(instance));
	}

	@Override
	public void reportMissingAssertion(ElkNamedIndividual instance,
			ElkClass type) {
		getListener()
				.missing(ELK_FACTORY.getClassAssertionAxiom(type, instance));
	}

	@Override
	public void reportMissingSameInstances(ElkNamedIndividual first,
			ElkNamedIndividual second) {
		getListener()
				.missing(ELK_FACTORY.getSameIndividualAxiom(first, second));
	}
}
