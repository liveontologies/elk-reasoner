/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link SubClassInclusionComposed} with {@link IndexedObjectUnionOf}
 * super-class obtained from a {@link SubClassInclusionComposed} in which the
 * super-class is one of its disjuncts.
 * 
 * @see IndexedObjectUnionOf#getDisjuncts()
 * 
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 *         
 * @author Yevgeny Kazakov        
 */
public class SubClassInclusionComposedObjectUnionOf
		extends
			AbstractSubClassInclusionComposedInference<IndexedObjectUnionOf> {

	private final IndexedClassExpression disjunct_;

	public SubClassInclusionComposedObjectUnionOf(IndexedContextRoot inferenceRoot,
			IndexedClassExpression premise, IndexedObjectUnionOf disjunction) {
		super(inferenceRoot, disjunction);
		disjunct_ = premise;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public SubClassInclusionComposed getPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getComposedSubClassInclusion(getInferenceRoot(),
				disjunct_);
	}

	@Override
	public final <O> O accept(
			SubClassInclusionComposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(SubClassInclusionComposedObjectUnionOf inference);
		
	}

}
