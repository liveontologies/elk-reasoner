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
 * @author Yevgeny Kazakov, Jul 3, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkEntity;
import org.semanticweb.elk.syntax.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.ElkSubObjectPropertyOfAxiom;

/**
 * Interface for methods required for index creation.
 * 
 * @author Yevgeny Kazakov
 *
 */
abstract class OntologyIndexModifier implements OntologyIndex {
	
	abstract IndexedEntity createIndexed(
			ElkEntity representative);
	
	abstract IndexedClassExpression createIndexed(
			ElkClassExpression representative);

	abstract IndexedObjectProperty createIndexed(
			ElkObjectPropertyExpression representative);
	
	abstract IndexedPropertyComposition createIndexed(
			ElkSubObjectPropertyOfAxiom representative);
	
	abstract void remove(IndexedClassExpression ice);
	 
	abstract void remove(IndexedObjectProperty iop);
	
	abstract void remove(IndexedPropertyComposition ipc);
	
}
