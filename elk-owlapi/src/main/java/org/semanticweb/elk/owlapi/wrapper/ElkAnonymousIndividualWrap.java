/*
 * #%L
 * ELK OWL API Binding
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

/**
 * Implements the {@link ElkAnonymousIndividual} interface by wrapping instances
 * of {@link OWLAnonymousIndividual}
 * 
 * @author Yevgeny Kazakov
 */
public class ElkAnonymousIndividualWrap<T extends OWLAnonymousIndividual>
		extends ElkIndividualWrap<T> implements ElkAnonymousIndividual {

	ElkAnonymousIndividualWrap(T owlAnonymousIndividual) {
		super(owlAnonymousIndividual);
	}

	public String getNodeId() {
		return this.owlObject.getID().toString();
	}

	@Override
	public <O> O accept(ElkIndividualVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
