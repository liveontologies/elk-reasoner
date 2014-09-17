/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;

/**
 * Certain kinds of indexed objects are created via this factory, usually those
 * which require different implementations. One example of different
 * implementations arises when we may or may need to store axiom bindings.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface IndexedObjectFactory {

	public IndexedSubClassOfAxiom createSubClassOfAxiom(
			IndexedClassExpression subClass, IndexedClassExpression superClass,
			ElkAxiom axiom);

	public IndexedDisjointnessAxiom createDisjointnessAxiom(
			List<IndexedClassExpression> disjointClasses, ElkAxiom axiom);

	public IndexedSubObjectPropertyOfAxiom<?> createdSubObjectPropertyOfAxiom(
			IndexedPropertyChain subChain, IndexedObjectProperty superProperty,
			ElkObjectPropertyAxiom axiom);

	public IndexedReflexiveObjectPropertyAxiom<?> createReflexiveObjectPropertyAxiom(
			IndexedObjectProperty property,
			ElkReflexiveObjectPropertyAxiom axiom);

	public IndexedObjectProperty createdIndexedObjectProperty(
			ElkObjectProperty property);

}
