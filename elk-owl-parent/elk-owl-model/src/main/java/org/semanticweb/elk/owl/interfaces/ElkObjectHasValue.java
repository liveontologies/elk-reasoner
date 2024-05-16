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
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.ElkObjectHasValueVisitor;

/**
 * Corresponds to an
 * <a href= "http://www.w3.org/TR/owl2-syntax/#Individual_Value_Restriction">
 * Individual Value Restriction for Object Properties</a> in the OWL 2
 * specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObjectHasValue extends
		ElkPropertyRestrictionQualified<ElkObjectPropertyExpression, ElkIndividual> {

	/**
	 * Accept an {@link ElkObjectHasValueVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this object type
	 * @param <O>
	 *            the type of the output of the visitor
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkObjectHasValueVisitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		/**
		 * Create an {@link ElkObjectHasValue}.
		 * 
		 * @param property
		 *            the {@link ElkObjectPropertyExpression} for which the
		 *            object should be created
		 * @param value
		 *            the {@link ElkIndividual} for which the object should be
		 *            created
		 * @return an {@link ElkObjectHasValue} corresponding to the input
		 */
		public ElkObjectHasValue getObjectHasValue(
				ElkObjectPropertyExpression property,
				ElkIndividual value);

	}
}
