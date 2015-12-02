package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents occurrences of an {@link ElkSubClassOfAxiom} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface IndexedSubClassOfAxiom extends IndexedAxiom {

	/**
	 * @return the {@link IndexedClassExpression} representing the sub class of
	 *         the {@link ElkSubClassOfAxiom} represented by this
	 *         {@link IndexedSubClassOfAxiom}
	 * 
	 * @see IndexedSubClassOfAxiom#getSubClass()
	 */
	IndexedClassExpression getSubClass();

	/**
	 * @return the {@link IndexedClassExpression} representing the super class
	 *         of the {@link ElkSubClassOfAxiom} represented by this
	 *         {@link IndexedSubClassOfAxiom}
	 * 
	 * @see IndexedSubClassOfAxiom#getSuperClass()
	 */
	IndexedClassExpression getSuperClass();
	
	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		public IndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
				ElkAxiom originalAxiom, IndexedClassExpression subClass,
				IndexedClassExpression superClass);

	}
	
	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedSubClassOfAxiom axiom);

	}
	
}
