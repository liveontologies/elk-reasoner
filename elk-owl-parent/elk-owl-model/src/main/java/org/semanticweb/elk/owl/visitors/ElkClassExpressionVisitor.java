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
package org.semanticweb.elk.owl.visitors;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;

/**
 * Visitor pattern interface for instances of {@link ElkClassExpression}.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public interface ElkClassExpressionVisitor<O> {

	O visit(ElkClass elkClass);

	O visit(ElkDataAllValuesFrom elkDataAllValuesFrom);

	O visit(ElkDataExactCardinality elkDataExactCardinality);

	O visit(ElkDataExactCardinalityQualified elkDataExactCardinalityQualified);

	O visit(ElkDataHasValue elkDataHasValue);

	O visit(ElkDataMaxCardinality elkDataMaxCardinality);

	O visit(ElkDataMaxCardinalityQualified elkDataMaxCardinalityQualified);

	O visit(ElkDataMinCardinality elkDataMinCardinality);

	O visit(ElkDataMinCardinalityQualified elkDataMinCardinalityQualified);

	O visit(ElkDataSomeValuesFrom elkDataSomeValuesFrom);

	O visit(ElkObjectAllValuesFrom elkObjectAllValuesFrom);

	O visit(ElkObjectComplementOf elkObjectComplementOf);

	O visit(ElkObjectExactCardinality elkObjectExactCardinality);

	O visit(ElkObjectExactCardinalityQualified elkObjectExactCardinalityQualified);

	O visit(ElkObjectHasSelf elkObjectHasSelf);

	O visit(ElkObjectHasValue elkObjectHasValue);

	O visit(ElkObjectIntersectionOf elkObjectIntersectionOf);

	O visit(ElkObjectMaxCardinality elkObjectMaxCardinality);

	O visit(ElkObjectMaxCardinalityQualified elkObjectMaxCardinalityQualified);

	O visit(ElkObjectMinCardinality elkObjectMinCardinality);

	O visit(ElkObjectMinCardinalityQualified elkObjectMinCardinalityQualified);

	O visit(ElkObjectOneOf elkObjectOneOf);

	O visit(ElkObjectSomeValuesFrom elkObjectSomeValuesFrom);

	O visit(ElkObjectUnionOf elkObjectUnionOf);

}
