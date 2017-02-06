package org.semanticweb.elk.reasoner.saturation.properties.inferences;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public abstract class AbstractSubPropertyChainInference extends
		AbstractObjectPropertyInference implements SubPropertyChainInference {

	private final IndexedPropertyChain subChain_, superChain_;

	public AbstractSubPropertyChainInference(IndexedPropertyChain subChain,
			IndexedPropertyChain superChain) {
		subChain_ = subChain;
		superChain_ = superChain;
	}

	public IndexedPropertyChain getSubChain() {
		return subChain_;
	}

	public IndexedPropertyChain getSuperChain() {
		return superChain_;
	}

	@Override
	public SubPropertyChain getConclusion(
			ObjectPropertyConclusion.Factory factory) {
		return factory.getSubPropertyChain(getSubChain(), getSuperChain());
	}

	@Override
	public final <O> O accept(ObjectPropertyInference.Visitor<O> visitor) {
		return accept((SubPropertyChainInference.Visitor<O>) visitor);
	}

}
