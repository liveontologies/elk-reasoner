/*
 * #%L
 * ELK OWL Object Interfaces
 * *
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
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.DatatypeVisitor;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Datatypes">Datatype<a> in the OWL 2
 * specification.
 *
 * @author Markus Kroetzsch
 * @author Pospishnyi Olexandr
 */
public interface ElkDatatype extends ElkDataRange, ElkEntity {

	public String getDatatypeShortname();

	public String getDatatypeIRI();

	/**
	 * Accept an {@link DatatypeVisitor}.
	 *
	 * @param visitor the visitor that can work with this datatype
	 * @return the output of the visitor
	 */
	public <O> O accept(DatatypeVisitor<O> visitor);

	/**
	 * Check weather this datatype is derived from another and shares its
	 * value space.
	 *
	 * @param datatype other datatype to check against
	 */
	public boolean isCompatibleWith(ElkDatatype datatype);
}
