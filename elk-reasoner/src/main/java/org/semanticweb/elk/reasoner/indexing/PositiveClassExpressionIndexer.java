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

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkClassExpressionVisitor;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;

/**
 * For indexing positive occurrences of class expressions.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
class PositiveClassExpressionIndexer implements
		ElkClassExpressionVisitor<IndexedClassExpression> {

	protected final AxiomIndexer axiomIndexer;

	protected PositiveClassExpressionIndexer(AxiomIndexer axiomIndexer) {
		this.axiomIndexer = axiomIndexer;
	}

	public IndexedClassExpression visit(ElkClass classExpression) {
		IndexedClass result = (IndexedClass) axiomIndexer.ontologyIndex.getCreateIndexedClassExpression(classExpression);
		result.positiveOccurrenceNo++;
		return result;
	}

	public IndexedClassExpression visit(ElkObjectIntersectionOf classExpression) {

		IndexedObjectIntersectionOf result = (IndexedObjectIntersectionOf)
			axiomIndexer.ontologyIndex.getCreateIndexedClassExpression(classExpression);
		if (result.positiveOccurrenceNo++ == 0) {
			for (ElkClassExpression d : classExpression.getClassExpressions()) {
				result.addConjunct(d.accept(this));
			}
		}
		return result;
	}
	
	public IndexedClassExpression visit(ElkObjectSomeValuesFrom classExpression) {

		IndexedObjectSomeValuesFrom result = (IndexedObjectSomeValuesFrom) 
			axiomIndexer.ontologyIndex.getCreateIndexedClassExpression(classExpression);
		if (result.positiveOccurrenceNo++ == 0) {
			result.setRelation(classExpression.getObjectPropertyExpression().accept(
					axiomIndexer.objectPropertyExpressionIndexer));
			result.setFiller(classExpression.getClassExpression().accept(this));
		}
		return result;
	}
}
