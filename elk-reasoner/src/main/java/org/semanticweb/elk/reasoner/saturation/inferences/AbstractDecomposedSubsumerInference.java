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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.DecomposedSubsumerInferenceVisitor;

public abstract class AbstractDecomposedSubsumerInference extends
		DecomposedSubsumerImpl implements DecomposedSubsumerInference {

	public AbstractDecomposedSubsumerInference(IndexedContextRoot root,
			IndexedClassExpression subsumer) {
		super(root, subsumer);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((DecomposedSubsumerInferenceVisitor<I, O>) visitor,
				parameter);
	}

}
