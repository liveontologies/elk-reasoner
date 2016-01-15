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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link DisjointSubsumer} obtained from a {@link SubClassInclusionComposed}
 * with the corresponding super-class.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class DisjointSubsumerFromSubsumer extends
		AbstractDisjointSubsumerInference {

	public DisjointSubsumerFromSubsumer(IndexedContextRoot inferenceRoot,
			IndexedClassExpressionList disjoint, int position, ElkAxiom reason) {
		super(inferenceRoot, disjoint, position, reason);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public SubClassInclusionComposed getPremise(SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getInferenceRoot(),
				getDisjointExpressions().getElements().get(getPosition()));
	}

	@Override
	public final <O> O accept(DisjointSubsumerInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(DisjointSubsumerFromSubsumer inference);
		
	}

}
