/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkClassExpressionVisitor.java 300 2011-08-10 16:52:49Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkClassExpressionVisitor.java $
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
package org.semanticweb.elk.syntax.visitors;

import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.syntax.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.syntax.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.syntax.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.interfaces.ElkObjectUnionOf;

/**
 * Visitor pattern interface for instances of {@link ElkClassExpression}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public interface ElkClassExpressionVisitor<O> {

	O visit(ElkClass elkClass);

	O visit(ElkObjectAllValuesFrom elkObjectAllValuesFrom);
	
	O visit(ElkObjectComplementOf elkObjectComplementOf);

	O visit(ElkObjectExactCardinality elkObjectExactCardinality);

	O visit(ElkObjectHasSelf elkObjectHasSelf);

	O visit(ElkObjectHasValue elkObjectHasValue);

	O visit(ElkObjectIntersectionOf elkObjectIntersectionOf);

	O visit(ElkObjectMaxCardinality elkObjectMaxCardinality);

	O visit(ElkObjectMinCardinality elkObjectMaxCardinality);

	O visit(ElkObjectOneOf elkObjectOneOf);

	O visit(ElkObjectSomeValuesFrom elkObjectSomeValuesFrom);

	O visit(ElkObjectUnionOf elkObjectUnionOf);

}
