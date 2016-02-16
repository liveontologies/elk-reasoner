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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link ClassInference} producing a {@link ClassInconsistency} from
 * {@link SubClassInclusionComposed} with
 * {@link SubClassInclusionComposed#getSubsumer()} equal to {@code owl:Nothing}:<br>
 * 
 * <pre>
 *  [C] ⊑ +⊥
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  [C] ⊑ 0
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()}={@link #getDestination()}<br>
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public class ClassInconsistencyOfOwlNothing
		extends
			AbstractClassInconsistencyOfInconsistentSubsumerInference<IndexedClass> {

	public ClassInconsistencyOfOwlNothing(IndexedContextRoot inferenceRoot,
			IndexedClass premiseSubsumer) {
		super(inferenceRoot, premiseSubsumer);
	}

	@Override
	public String toString() {
		return super.toString() + " from owl:Nothing";
	}

	@Override
	public final <O> O accept(ClassConclusion.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(ClassInconsistencyInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(ClassInconsistencyOfOwlNothing inference);

	}

}
