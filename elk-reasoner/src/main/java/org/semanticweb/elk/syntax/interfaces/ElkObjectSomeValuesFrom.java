/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Existential_Quantification">Existential
 * Quantification Object Property Restriction<a> in the OWL 2 specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObjectSomeValuesFrom extends ElkClassExpression {

	/**
	 * Get the object property expression that this expression refers to.
	 * 
	 * @return object property expression
	 */
	public ElkObjectPropertyExpression getObjectPropertyExpression();

	/**
	 * Get the class expression that this expression refers to.
	 * 
	 * @return class expression
	 */
	public ElkClassExpression getClassExpression();

}
