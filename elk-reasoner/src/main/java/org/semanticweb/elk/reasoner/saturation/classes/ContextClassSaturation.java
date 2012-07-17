/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.saturation.classes;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;

/**
 * This interface is implemented by {@link Context}s that are used in computing
 * and storing superclass relationships. They provide basic methods for
 * accessing this information.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface ContextClassSaturation extends Context {

	/**
	 * Get the set of super class expressions that have been derived for this
	 * context so far.
	 * 
	 * @return the set of derived indexed class expressions
	 */
	public Set<IndexedClassExpression> getSuperClassExpressions();

	/**
	 * Mark context as saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed.
	 */
	public void setSaturated();

	/**
	 * Returns {@code true} if context is saturated. A context is saturated if
	 * all superclass expressions of the root expression have been computed.
	 * This needs to be set explicitly by some processor.
	 * 
	 * @return {@code true} if this context is saturated
	 */
	public boolean isSaturated();

	/**
	 * @return {@code true} if this context is known to be satisfiable
	 */
	public boolean isSatisfiable();

	/**
	 * Set the satisfiability flag of this context.
	 * 
	 * @param satisfiable
	 */
	public void setSatisfiable(boolean satisfiable);

}
