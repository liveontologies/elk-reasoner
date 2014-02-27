/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceVisitor;

/**
 * Represents a {@link IndexedObjectIntersectionOf} composition inference.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ComposedConjunction extends ComposedSubsumer implements Inference {

	private final IndexedClassExpression first_;
	
	private final IndexedClassExpression second_;
	
	/**
	 */
	public ComposedConjunction(IndexedClassExpression premise, IndexedClassExpression conjunct, IndexedObjectIntersectionOf conjunction) {
		super(conjunction);
		first_ = premise;
		second_ = conjunct;
	}

	public Subsumer getFirstConjunct() {
		return new ComposedSubsumer(first_);
	}
	
	public Subsumer getSecondConjunct() {
		return new ComposedSubsumer(second_);
	}
	
	@Override
	public <I, O> O acceptTraced(InferenceVisitor<I, O> visitor, I parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public IndexedClassExpression getInferenceContextRoot(IndexedClassExpression rootWhereStored) {
		return rootWhereStored;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (conjunction+)";
	}

}
