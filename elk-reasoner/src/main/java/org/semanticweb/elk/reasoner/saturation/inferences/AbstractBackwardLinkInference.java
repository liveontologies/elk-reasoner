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
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.tracing.Inference;

public abstract class AbstractBackwardLinkInference extends BackwardLinkImpl
		implements
			BackwardLinkInference {

	public AbstractBackwardLinkInference(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedContextRoot source) {
		super(root, relation, source);
	}

	@Override
	public <O> O accept(Inference.Visitor<O> visitor) {
		return accept((BackwardLinkInference.Visitor<O>) visitor);
	}
	
	@Override
	public <O> O accept(SaturationInference.Visitor<O> visitor) {
		return accept((BackwardLinkInference.Visitor<O>) visitor);
	}

	@Override
	public <O> O accept(ClassInference.Visitor<O> visitor) {
		return accept((BackwardLinkInference.Visitor<O>) visitor);
	}	

}
