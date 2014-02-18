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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;

/**
 * Represents a decomposition of {@link IndexedObjectSomeValuesFrom} which
 * creates a {@link BackwardLink}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class DecomposedExistential extends BackwardLink implements Inference {

	private final IndexedClassExpression inferenceContext_;
	
	private final IndexedObjectSomeValuesFrom existential_;
	
	/**
	 * 
	 */
	public DecomposedExistential(IndexedObjectSomeValuesFrom subsumer, IndexedClassExpression source) {
		super(source, subsumer.getRelation());
		existential_ = subsumer;
		inferenceContext_ = source;
	}
	
	@Override
	public <R, C> R acceptTraced(InferenceVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	public Subsumer getExistential() {
		return new DecomposedSubsumer(existential_);
	}

	@Override
	public IndexedClassExpression getInferenceContextRoot(IndexedClassExpression rootWhereStored) {
		return inferenceContext_;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (decomposition)";
	}
}
