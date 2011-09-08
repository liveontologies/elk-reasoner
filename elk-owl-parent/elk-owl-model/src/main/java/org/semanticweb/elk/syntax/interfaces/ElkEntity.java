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
/**
 * @author Markus Kroetzsch, Aug 8, 2011
 */
package org.semanticweb.elk.syntax.interfaces;

import org.semanticweb.elk.syntax.visitors.ElkEntityVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Entities.2C_Literals.2C_and_Anonymous_Individuals"
 * >Entity<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkEntity {

	/**
	 * Get the IRI of this entity.
	 * 
	 * @return The IRI of this entity.
	 */
	public String getIri();

	/**
	 * Accept an ElkEntityVisitor.
	 * 
	 * @param visitor
	 * @return
	 */
	public <O> O accept(ElkEntityVisitor<O> visitor);

}
