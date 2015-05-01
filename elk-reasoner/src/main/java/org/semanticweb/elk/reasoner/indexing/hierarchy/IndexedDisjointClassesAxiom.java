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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;

/**
 * Represents occurrences of an {@link ElkDisjointClassesAxiom} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedDisjointClassesAxiom extends IndexedAxiom {

	/**
	 * @return {@link IndexedClassExpression}s corresponding to
	 *         {@link ElkClassExpression}s that occur at least twice in this
	 *         {@link IndexedDisjointClassesAxiom}
	 * 
	 * @see ElkDisjointClassesAxiom#getClassExpressions()
	 */
	public Set<? extends IndexedClassExpression> getInconsistentMembers();

	/**
	 * @return {@link IndexedClassExpression}s corresponding to
	 *         {@link ElkClassExpression}s that occur exactly once in this
	 *         {@link IndexedDisjointClassesAxiom}
	 * 
	 * @see ElkDisjointClassesAxiom#getClassExpressions()
	 */
	public Set<? extends IndexedClassExpression> getDisjointMembers();

	/**
	 * @return {@code true} if this {@link IndexedDisjointClassesAxiom} occurs
	 *         in the ontology
	 */
	public boolean occurs();

}
