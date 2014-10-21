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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * A collection of method for indexing ELK objects
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ElkAxiomIndexer {

	public void indexSubClassOfAxiom(ElkClassExpression subClass,
			ElkClassExpression superClass);

	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty);

	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type);

	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> list);

	public void indexReflexiveObjectProperty(
			ElkObjectPropertyExpression reflexiveProperty);

	public IndexedClass indexClassDeclaration(ElkClass ec);

	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty eop);

	public IndexedIndividual indexNamedIndividualDeclaration(
			ElkNamedIndividual eni);
}
