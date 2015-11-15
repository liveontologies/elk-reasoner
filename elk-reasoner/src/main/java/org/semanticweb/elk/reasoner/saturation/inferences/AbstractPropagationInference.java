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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.PropagationImpl;

public abstract class AbstractPropagationInference extends PropagationImpl
		implements PropagationInference {

	public AbstractPropagationInference(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedObjectSomeValuesFrom carry) {
		super(root, relation, carry);
	}

	@Override
	public <I, O> O accept(SaturationInference.Visitor<I, O> visitor, I input) {
		return accept((PropagationInference.Visitor<I, O>) visitor, input);
	}
	
	@Override
	public <I, O> O accept(ClassInference.Visitor<I, O> visitor, I input) {
		return accept((PropagationInference.Visitor<I, O>) visitor, input);
	}
	

}
