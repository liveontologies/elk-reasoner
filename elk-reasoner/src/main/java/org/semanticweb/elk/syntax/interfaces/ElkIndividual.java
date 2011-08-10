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

import org.semanticweb.elk.syntax.ElkIndividualVisitor;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Individuals">Individual<a> in the OWL 2
 * specification.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkIndividual extends ElkObject {

	/**
	 * Accept an ElkIndividualVisitor.
	 * 
	 * @param visitor
	 * @return
	 */
	public abstract <O> O accept(ElkIndividualVisitor<O> visitor);

}
