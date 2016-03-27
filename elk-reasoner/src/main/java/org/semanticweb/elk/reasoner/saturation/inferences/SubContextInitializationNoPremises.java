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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * A {@link SubContextInitializationInference} that produces a
 * {@link SubContextInitialization} from no premises:<br>
 * 
 * <pre>
 * 
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  ![C:R]
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getDestination()}<br>
 * R = {@link #getSubDestination()}
 * 
 * @author "Yevgeny Kazakov"
 */
public class SubContextInitializationNoPremises
		extends
			AbstractSubContextInitializationInference {

	public SubContextInitializationNoPremises(IndexedContextRoot root,
			IndexedObjectProperty subRoot) {
		super(root, subRoot);
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	@Override
	public final <O> O accept(SubClassInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(
			SubContextInitializationInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(InitializationInference.Visitor<O> visitor) {
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

		public O visit(SubContextInitializationNoPremises inference);

	}

}
