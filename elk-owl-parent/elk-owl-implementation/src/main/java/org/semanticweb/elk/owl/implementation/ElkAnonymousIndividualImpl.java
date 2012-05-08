/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Anonymous_Individuals">Anonymous
 * Individuals<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkAnonymousIndividualImpl extends ElkObjectImpl implements
		ElkAnonymousIndividual {

	protected final String nodeId;

	/* package-private */ElkAnonymousIndividualImpl(String nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		return nodeId;
	}

	@Override
	public <O> O accept(ElkIndividualVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
