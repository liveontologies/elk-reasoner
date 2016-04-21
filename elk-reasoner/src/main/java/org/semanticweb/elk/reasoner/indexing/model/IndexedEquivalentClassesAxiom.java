package org.semanticweb.elk.reasoner.indexing.model;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

/**
 * An {@link IndexedAxiom} constructed from two {@link IndexedClassExpression}s.
 * <br>
 * 
 * Notation:
 * 
 * <pre>
 * [C = D]
 * </pre>
 * 
 * It is logically equivalent to the OWL axiom {@code EquivalentClasses(C D)}
 * <br>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getFirstMember()}<br>
 * D = {@link #getSecondMember()}<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedEquivalentClassesAxiom extends IndexedAxiom {

	/**
	 * @return the first of the two equivalent {@link IndexedClassExpression}s
	 */
	IndexedClassExpression getFirstMember();

	/**
	 * @return the first of the two equivalent {@link IndexedClassExpression}s
	 */
	IndexedClassExpression getSecondMember();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedEquivalentClassesAxiom axiom);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		IndexedEquivalentClassesAxiom getIndexedEquivalentClassesAxiom(
				ElkAxiom originalAxiom, IndexedClassExpression firstMember,
				IndexedClassExpression secondMember);

	}

}
