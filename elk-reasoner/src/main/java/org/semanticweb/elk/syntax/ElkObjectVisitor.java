/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.syntax;

/**
 * Visitor pattern interface for instances of {@link ElkObject}.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public interface ElkObjectVisitor<O> extends ElkAxiomVisitor<O>,
		ElkClassExpressionVisitor<O>, ElkObjectPropertyExpressionVisitor<O>,
		ElkIndividualVisitor<O> {

	O visit(ElkObjectPropertyChain elkObjectPropertyChain);

}
