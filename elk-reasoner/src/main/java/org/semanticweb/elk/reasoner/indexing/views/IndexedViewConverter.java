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
package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyComposition;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedSubPropertyExpressionVisitor;

public class IndexedViewConverter
		implements
		IndexedClassExpressionVisitor<IndexedClassExpressionView<? extends IndexedClassExpression>>,
		IndexedSubPropertyExpressionVisitor<IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression>> {

	private IndexedViewConverter() {
	}

	private static IndexedViewConverter instance_ = new IndexedViewConverter();

	public static IndexedViewConverter getInstance() {
		return instance_;
	}

	public IndexedClassExpressionView<IndexedClass> visit(IndexedClass element) {
		return new IndexedClassView<IndexedClass>(element);
	}

	public IndexedClassExpressionView<IndexedObjectIntersectionOf> visit(
			IndexedObjectIntersectionOf element) {
		return new IndexedObjectIntersectionOfView<IndexedObjectIntersectionOf>(
				element);
	}

	public IndexedClassExpressionView<IndexedObjectSomeValuesFrom> visit(
			IndexedObjectSomeValuesFrom element) {
		return new IndexedObjectSomeValuesFromView<IndexedObjectSomeValuesFrom>(
				element);
	}

	public IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> visit(
			IndexedObjectProperty element) {
		return new IndexedObjectPropertyView<IndexedObjectProperty>(element) {
		};
	}

	public IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> visit(
			IndexedPropertyComposition element) {
		return new IndexedPropertyCompositionView<IndexedPropertyComposition>(
				element);
	}
}
