/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObject.java 295 2011-08-10 11:43:29Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/interfaces/ElkObject.java $
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

import org.semanticweb.elk.syntax.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.StructuralHashObject;

/**
 * Basic implementation of hashable objects in ELK, typically syntactic
 * structures like axioms or class expressions. In addition to a structural hash
 * code that reflects the content of an ELKObject, this class also provides a
 * basic hash code that acts as an ID for the actual Java object and which is
 * used in managing such objects.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkObject extends StructuralHashObject {

	/**
	 * Compare the structure of two ELKObjects and return true if they are
	 * structurally equivalent.
	 * 
	 * @param object
	 * @return True if objects are structurally equal
	 */
	public abstract boolean structuralEquals(Object object);

	/**
	 * Accept an ElkObjectVisitor.
	 * 
	 * @param visitor
	 * @return
	 */
	public abstract <O> O accept(ElkObjectVisitor<O> visitor);

}
