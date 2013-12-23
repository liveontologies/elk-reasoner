package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SubsumerDecompositionVisitor} wrapper for a given
 * {@link SubsumerDecompositionVisitor} that additionally records the
 * number of invocations of the methods using the given
 * {@link DecompositionRuleApplicationCounter}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class DecompositionRuleApplicationCounterVisitor implements
		SubsumerDecompositionVisitor {

	/**
	 * the visitor whose method applications to be counted
	 */
	private final SubsumerDecompositionVisitor visitor_;
	/**
	 * the counter used to count the number of method applications of the
	 * visitor
	 */
	private final DecompositionRuleApplicationCounter counter_;

	/**
	 * Creates a new {@link SubsumerDecompositionVisitor} that executes
	 * the corresponding methods of the given
	 * {@link SubsumerDecompositionVisitor} and counts the number of
	 * invocations of the corresponding methods using the given
	 * {@link DecompositionRuleApplicationCounter}.
	 * 
	 * @param visitor
	 *            the {@link SubsumerDecompositionVisitor} used to
	 *            execute the methods
	 * @param counter
	 *            the {@link DecompositionRuleApplicationCounter} used to count
	 *            the number of method invocations
	 */
	public DecompositionRuleApplicationCounterVisitor(
			SubsumerDecompositionVisitor visitor,
			DecompositionRuleApplicationCounter counter) {
		this.visitor_ = visitor;
		this.counter_ = counter;
	}

	@Override
	public void visit(IndexedClass ice, Context context) {
		counter_.countIndexedClassDecompositionRule++;
		visitor_.visit(ice, context);
	}

	@Override
	public void visit(IndexedDataHasValue ice, Context context) {
		counter_.countIndexedDataHasValueDecompositionRule++;
		visitor_.visit(ice, context);
	}

	@Override
	public void visit(IndexedIndividual idv, Context context) {
		// not supported		
	}

	@Override
	public void visit(IndexedObjectComplementOf ice, Context context) {
		counter_.countIndexedObjectComplementOfDecompositionRule++;
		visitor_.visit(ice, context);
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice, Context context) {
		counter_.countIndexedObjectIntersectionOfDecompositionRule++;
		visitor_.visit(ice, context);
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom ice, Context context) {
		counter_.countIndexedObjectSomeValuesFromDecompositionRule++;
		visitor_.visit(ice, context);
	}

	@Override
	public void visit(IndexedObjectUnionOf ice, Context context) {
		// not supported
		
	}

}
