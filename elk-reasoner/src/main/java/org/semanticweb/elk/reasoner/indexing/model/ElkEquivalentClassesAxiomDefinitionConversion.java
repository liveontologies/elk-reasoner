package org.semanticweb.elk.reasoner.indexing.model;

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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;

/**
 * Represents a transformation of an {@link ElkEquivalentClassesAxiom} to an
 * {@link IndexedDefinitionAxiom} representing that one {@link ElkClass} that is
 * a member of the {@link ElkEquivalentClassesAxiom} is defined to be equivalent
 * to another {@link ElkClassExpression} member.
 * 
 * @see ElkEquivalentClassesAxiom#getClassExpressions()
 * 
 * @author Yevgeny Kazakov
 */
public interface ElkEquivalentClassesAxiomDefinitionConversion
		extends
			IndexedDefinitionAxiomInference {

	@Override
	ElkEquivalentClassesAxiom getOriginalAxiom();

	/**
	 * @return the position of the {@link ElkClass} in the member list of the
	 *         {@link ElkEquivalentClassesAxiom} that is converted to the
	 *         defined class of the resulting {@link IndexedDefinitionAxiom}
	 * 
	 * @see ElkEquivalentClassesAxiom#getClassExpressions()
	 * @see IndexedDefinitionAxiom#getDefinedClass()
	 */
	int getDefinedClassPosition();

	/**
	 * @return the position of the {@link ElkClassExpression} in the member list
	 *         of the {@link ElkEquivalentClassesAxiom} that is converted to the
	 *         definition of the resulting {@link IndexedDefinitionAxiom}
	 * 
	 * @see ElkEquivalentClassesAxiom#getClassExpressions()
	 * @see IndexedDefinitionAxiom#getDefinition()
	 */
	int getDefinitionPosition();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(ElkEquivalentClassesAxiomDefinitionConversion inference);
		
	}
	
}
