/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.ElkDataPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeDefinitionAxiomVisitor;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Datatype_Definitions">Datatype
 * Definitions<a> in the OWL 2 specification.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 *
 */
public interface ElkDatatypeDefinitionAxiom extends ElkAxiom {

	public ElkDatatype getDatatype();

	public ElkDataRange getDataRange();

	/**
	 * Accept an {@link ElkDataPropertyAxiomVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkDatatypeDefinitionAxiomVisitor<O> visitor);
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkDatatypeDefinitionAxiom}
		 * 
		 * @param datatype
		 *            the {@link ElkDatatype} for which the axiom should be created
		 * @param dataRange
		 *            the {@link ElkDataRange} for which the axiom should be created
		 * @return an {@link ElkDatatypeDefinitionAxiom} corresponding to the input
		 */
		public ElkDatatypeDefinitionAxiom getDatatypeDefinitionAxiom(
				ElkDatatype datatype, ElkDataRange dataRange);

	}

}
