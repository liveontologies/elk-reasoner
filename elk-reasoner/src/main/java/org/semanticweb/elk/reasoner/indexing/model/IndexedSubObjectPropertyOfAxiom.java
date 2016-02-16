package org.semanticweb.elk.reasoner.indexing.model;

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

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * An {@link IndexedAxiom} constructed from an {@link IndexedPropertyChain} and
 * an {@link IndexedObjectProperty}.<br>
 * 
 * Notation:
 * 
 * <pre>
 * [P ⊑ R]
 * </pre>
 * 
 * It is logically equivalent to the OWL axiom {@code SubObjectPropertyOf(P R)}
 * <br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * P = {@link #getSubPropertyChain()}<br>
 * R = {@link #getSuperProperty()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedSubObjectPropertyOfAxiom extends IndexedAxiom {

	/**
	 * @return the {@link IndexedPropertyChain} representing the sub property
	 *         expression of the {@link ElkSubObjectPropertyOfAxiom} represented
	 *         by this {@link IndexedSubObjectPropertyOfAxiom}
	 * 
	 * @see ElkSubObjectPropertyOfAxiom#getSubObjectPropertyExpression()
	 */
	IndexedPropertyChain getSubPropertyChain();

	/**
	 * @return the {@link IndexedObjectProperty} representing the super property
	 *         of the {@link ElkSubObjectPropertyOfAxiom} represented by this
	 *         {@link IndexedSubObjectPropertyOfAxiom}
	 * 
	 * @see ElkSubObjectPropertyOfAxiom#getSuperObjectPropertyExpression()
	 */
	IndexedObjectProperty getSuperProperty();

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		IndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
				ElkAxiom originalAxiom, IndexedPropertyChain subPropertyChain,
				IndexedObjectProperty superProperty);

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

		O visit(IndexedSubObjectPropertyOfAxiom axiom);

	}

}
