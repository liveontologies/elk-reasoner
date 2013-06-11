package org.semanticweb.elk.reasoner.indexing.visitors;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;

public class AbstractIndexedObjectFilter implements
		IndexedObjectFilter {

	@Override
	public IndexedClass visit(IndexedClass element) {
		return element;
	}

	@Override
	public IndexedIndividual visit(IndexedIndividual element) {
		return element;
	}

	@Override
	public IndexedObjectIntersectionOf visit(IndexedObjectIntersectionOf element) {
		return element;
	}

	@Override
	public IndexedObjectSomeValuesFrom visit(IndexedObjectSomeValuesFrom element) {
		return element;
	}

	@Override
	public IndexedDatatypeExpression visit(IndexedDatatypeExpression element) {
		return element;
	}

	@Override
	public IndexedObjectProperty visit(IndexedObjectProperty element) {
		return element;
	}
        
        @Override
	public IndexedDataProperty visit(IndexedDataProperty element) {
		return element;
	}

	@Override
	public IndexedBinaryPropertyChain visit(IndexedBinaryPropertyChain element) {
		return element;
	}

	@Override
	public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
		return axiom;
	}

	@Override
	public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
		return axiom;
	}

}
