package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPredefinedClass;

/**
 * An {@link IndexedClassExpression.Visitor} that always returns {@code null}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <O>
 */
public class DummyIndexedClassExpressionVisitor<O> implements
		IndexedClassExpression.Visitor<O> {

	@SuppressWarnings("unused")
	protected O defaultVisit(IndexedClassExpression element) {
		return null;
	}
	
	protected O defaultVisit(IndexedComplexClassExpression element) {
		return defaultVisit((IndexedClassExpression) element);
	}
	
	protected O defaultVisit(IndexedClassEntity element) {
		return defaultVisit((IndexedClassExpression) element);
	}
	
	protected O defaultVisit(IndexedClass element) {
		return defaultVisit((IndexedClassEntity) element);
	}

	@Override
	public O visit(IndexedDataHasValue element) {
		return defaultVisit(element);
	}
	
	@Override
	public O visit(IndexedDefinedClass element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedIndividual element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedObjectComplementOf element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedObjectHasSelf element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedObjectIntersectionOf element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedObjectSomeValuesFrom element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedObjectUnionOf element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedPredefinedClass element) {
		return defaultVisit(element);
	}

}
