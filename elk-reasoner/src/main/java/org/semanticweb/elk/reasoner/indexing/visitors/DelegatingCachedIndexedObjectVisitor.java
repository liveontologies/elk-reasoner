package org.semanticweb.elk.reasoner.indexing.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;

public class DelegatingCachedIndexedObjectVisitor<O> implements
		CachedIndexedObjectVisitor<O> {

	private final IndexedClassExpressionVisitor<O> classExpressionVisitor_;

	private final IndexedPropertyChainVisitor<O> propertyChainVisitor_;

	private final IndexedDisjointnessAxiomVisitor<O> disjointnessAxiomVisitor_;

	public DelegatingCachedIndexedObjectVisitor(
			IndexedClassExpressionVisitor<O> classExpressionVisitor,
			IndexedPropertyChainVisitor<O> propertyChainVisitor,
			IndexedDisjointnessAxiomVisitor<O> disjointnessAxiomVisitor) {
		this.classExpressionVisitor_ = classExpressionVisitor;
		this.propertyChainVisitor_ = propertyChainVisitor;
		this.disjointnessAxiomVisitor_ = disjointnessAxiomVisitor;
	}

	@Override
	public O visit(IndexedClass element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedObjectUnionOf element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedObjectComplementOf element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedObjectIntersectionOf element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedObjectSomeValuesFrom element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedDataHasValue element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedIndividual element) {
		return classExpressionVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedObjectProperty element) {
		return propertyChainVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedBinaryPropertyChain element) {
		return propertyChainVisitor_.visit(element);
	}

	@Override
	public O visit(IndexedDisjointnessAxiom axiom) {
		return disjointnessAxiomVisitor_.visit(axiom);
	}

}