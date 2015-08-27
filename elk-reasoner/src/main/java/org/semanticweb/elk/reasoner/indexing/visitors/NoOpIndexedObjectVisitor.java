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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedRangeFiller;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubObjectPropertyOfAxiom;

/**
 * An {@link IndexedObjectVisitor} that always returns {@code null}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <O>
 */
public class NoOpIndexedObjectVisitor<O> implements IndexedObjectVisitor<O> {

	@SuppressWarnings("unused")
	protected O defaultVisit(IndexedObject element) {
		return null;
	}

	@Override
	public O visit(IndexedClass element) {
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
	public O visit(IndexedDataHasValue element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedObjectProperty element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedComplexPropertyChain element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedRangeFiller element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedSubClassOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedSubObjectPropertyOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedObjectPropertyRangeAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedReflexiveObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedDisjointClassesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedDeclarationAxiom axiom) {
		return defaultVisit(axiom);
	}	

}
