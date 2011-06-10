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
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;

/**
 * For indexing negative occurrences of class expressions.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
class NegativeClassExpressionIndexer implements
		ElkClassExpressionVisitor<IndexedClassExpression> {

	protected final AxiomIndexer axiomIndexer;
	
	NegativeClassExpressionIndexer(AxiomIndexer axiomIndexer) {
		this.axiomIndexer = axiomIndexer;
	}

	public IndexedClassExpression visit(ElkClass elkClass) {
		IndexedClass result = (IndexedClass) axiomIndexer.ontologyIndex.getCreateIndexedClassExpression(elkClass);
		result.negativeOccurrenceNo++;
		return result;
	}

	public IndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {

		IndexedObjectIntersectionOf result = (IndexedObjectIntersectionOf) 
			axiomIndexer.ontologyIndex.getCreateIndexedClassExpression(elkObjectIntersectionOf);
		if (result.negativeOccurrenceNo++ == 0) {

			int conjunctionSize = elkObjectIntersectionOf.getClassExpressions()
					.size();
			assert conjunctionSize > 1;
			int i = 0;
			IndexedClassExpression prefixConjunction = null;
			for (ElkClassExpression element : elkObjectIntersectionOf
					.getClassExpressions()) {
				i++;
				IndexedClassExpression indexedElement = element.accept(this);
				if (i == 1) {
					prefixConjunction = indexedElement;
					continue;
				} else if (i < conjunctionSize) {
					List<ElkClassExpression> arguments = new ArrayList<ElkClassExpression>(
							2);
					arguments.add(prefixConjunction.getClassExpression());
					arguments.add(element);
					prefixConjunction = ElkObjectIntersectionOf.create(
							arguments).accept(this);
				} else {
					prefixConjunction.addNegativeConjunctionByConjunct(result, indexedElement);
					indexedElement.addNegativeConjunctionByConjunct(result, prefixConjunction);
				}
			}
		}
		return result;
	}

	
	public IndexedClassExpression visit(ElkObjectSomeValuesFrom classExpression) {
		
		IndexedObjectSomeValuesFrom result = (IndexedObjectSomeValuesFrom)
			axiomIndexer.ontologyIndex.getCreateIndexedClassExpression(classExpression);
		if (result.negativeOccurrenceNo++ == 0) {
			result.setRelation(classExpression.getObjectPropertyExpression().accept(
					axiomIndexer.objectPropertyExpressionIndexer));
			result.setFiller(classExpression.getClassExpression().accept(this));
			result.getFiller().addNegativeExistential(result);
		}
		return result;
	}

}
