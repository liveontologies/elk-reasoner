/*
 * #%L
 * ELK OWL Object Interfaces
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
package org.semanticweb.elk.owl.interfaces;


/**
 * Corresponds to a pair of constraining facet and restriction value as used in
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Datatype_Restrictions">OWL 2
 * Datatype Restrictions<a>.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkFacetRestriction extends ElkObject {

	/**
	 * Get the IRI of the constraining facet.
	 * 
	 * @return IRI of the facet
	 */
	public String getConstrainingFacet();

	/**
	 * Get the short name of the constraining facet.
	 *
	 * @return short name of the facet
	 */
	public String getConstrainingFacetShortName();

	/**
	 * Get the literal used as a restriction value.
	 * 
	 * @return the literal used as restriction value
	 */
	public ElkLiteral getRestrictionValue();
}
