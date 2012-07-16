/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkObjectVisitor.java 282 2011-08-08 17:06:32Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkObjectVisitor.java $
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
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Visitor pattern interface for instances of {@link ElkObject}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Frantisek Simancik
 * 
 * @param <O>
 *            the output type of the visitor
 */
public interface ElkObjectVisitor<O> extends ElkAxiomVisitor<O>,
		ElkClassExpressionVisitor<O>, ElkSubObjectPropertyExpressionVisitor<O>,
		ElkDataPropertyExpressionVisitor<O>, ElkIndividualVisitor<O>,
		ElkLiteralVisitor<O>, ElkEntityVisitor<O>, ElkDataRangeVisitor<O>,
		ElkFacetRestrictionVisitor<O>, ElkAnnotationVisitor<O>,
		ElkAnnotationSubjectVisitor<O>, ElkAnnotationValueVisitor<O> {
}
