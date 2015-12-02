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
package org.semanticweb.elk.reasoner.reduction;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

/**
 * The visitor pattern interface to distinguish the types of the transitive
 * reduction output.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
/**
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input of the {@link TransitiveReductionJob}s for
 *            which the output can be accepted by this visitor
 */
public interface TransitiveReductionOutputVisitor<R extends IndexedClassExpression> {

	public void visit(TransitiveReductionOutputEquivalentDirect<R> output);

	public void visit(TransitiveReductionOutputEquivalent<R> output);

	public void visit(TransitiveReductionOutputUnsatisfiable<R> output);
}
