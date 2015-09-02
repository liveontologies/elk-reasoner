/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

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

/**
 * A visitor over the {@link ClassInference} hierarchy.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ClassInferenceVisitor<I, O> extends
		BackwardLinkInferenceVisitor<I, O>,
		ContradictionInferenceVisitor<I, O>,
		DisjointSubsumerInferenceVisitor<I, O>,
		ForwardLinkInferenceVisitor<I, O>, PropagationInferenceVisitor<I, O>,
		ComposedSubsumerInferenceVisitor<I, O>,
		DecomposedSubsumerInferenceVisitor<I, O> {

	public static final ClassInferenceVisitor<?, ?> DUMMY = new AbstractClassInferenceVisitor<Void, Void>() {

		@Override
		protected Void defaultTracedVisit(ClassInference conclusion, Void input) {
			return null;
		}

	};

}
