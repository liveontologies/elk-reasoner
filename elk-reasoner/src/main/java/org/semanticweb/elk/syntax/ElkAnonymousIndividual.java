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
package org.semanticweb.elk.syntax;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Anonymous_Individuals">Anonymous
 * Individuals<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public class ElkAnonymousIndividual extends ElkIndividual {

	protected final String nodeId;

	private ElkAnonymousIndividual(String nodeId) {
		this.nodeId = nodeId;
		this.structuralHashCode = nodeId.hashCode();
	}

	public static ElkAnonymousIndividual create(String nodeId) {
		return (ElkAnonymousIndividual) factory.put(new ElkAnonymousIndividual(nodeId));
	}

	/**
	 * Get the nodeID of this anonymous individual.
	 * 
	 * @return The nodeID of anonymous individual.
	 */
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		return nodeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ElkObject#structuralEquals(java.lang.Object)
	 */
	@Override
	public boolean structuralEquals(ElkObject object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkAnonymousIndividual) {
			return nodeId.equals(((ElkAnonymousIndividual) object).nodeId);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKIndividual#accept(org.semanticweb
	 * .elk.reasoner.ELKIndividualVisitor)
	 */
	@Override
	public <O> O accept(ElkIndividualVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
