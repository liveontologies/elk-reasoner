package org.semanticweb.elk.reasoner.indexing.inferences;

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

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubObjectPropertyOfAxiom;

/**
 * Represents a transformation of an {@link ElkEquivalentObjectPropertiesAxiom}
 * to an {@link IndexedSubObjectPropertyOfAxiom} representing the inclusion
 * between two selected members of the {@link ElkEquivalentClassesAxiom}.
 * 
 * @see ElkEquivalentObjectPropertiesAxiom#getObjectPropertyExpressions()
 * 
 * @author Yevgeny Kazakov
 */
public interface ElkEquivalentObjectPropertiesAxiomConversion
		extends
			IndexedSubObjectPropertyOfAxiomInference {

	@Override
	ElkEquivalentObjectPropertiesAxiom getOriginalAxiom();

	/**
	 * @return the position of an {@link ElkDataPropertyExpression} in the
	 *         member list of the {@link ElkEquivalentObjectPropertiesAxiom}
	 *         that is converted to the sub-property chain of the
	 *         {@link IndexedSubObjectPropertyOfAxiom}.
	 * 
	 * @see ElkEquivalentObjectPropertiesAxiom#getObjectPropertyExpressions()
	 * @see IndexedSubObjectPropertyOfAxiom#getSubPropertyChain()
	 */
	int getSubPropertyPosition();

	/**
	 * @return the position of an {@link ElkDataPropertyExpression} in the
	 *         member list of the {@link ElkEquivalentObjectPropertiesAxiom}
	 *         that is converted to the super-property of the
	 *         {@link IndexedSubObjectPropertyOfAxiom}.
	 * 
	 * @see ElkEquivalentObjectPropertiesAxiom#getObjectPropertyExpressions()
	 * @see IndexedSubObjectPropertyOfAxiom#getSuperProperty()
	 */
	int getSuperPropertyPosition();

}
