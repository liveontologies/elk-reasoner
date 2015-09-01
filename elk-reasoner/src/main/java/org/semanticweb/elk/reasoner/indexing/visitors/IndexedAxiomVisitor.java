package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedAxiom;

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
 * Visitor pattern interface for instances of {@link IndexedAxiom}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of the output of this visitor
 */
public interface IndexedAxiomVisitor<O> extends
		IndexedDisjointClassesAxiomVisitor<O>,
		IndexedSubClassOfAxiomVisitor<O>, IndexedDefinitionAxiomVisitor<O>,
		IndexedSubObjectPropertyOfAxiomVisitor<O>,
		IndexedObjectPropertyRangeAxiomVisitor<O>,
		IndexedDeclarationAxiomVisitor<O> {

	// combined visitor
}
