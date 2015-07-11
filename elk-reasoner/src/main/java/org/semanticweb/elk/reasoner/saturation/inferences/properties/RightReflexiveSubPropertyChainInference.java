/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.properties;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class RightReflexiveSubPropertyChainInference extends
		ReflexiveSubPropertyChainInference<IndexedObjectProperty> {

	public RightReflexiveSubPropertyChainInference(
			IndexedComplexPropertyChain sub, IndexedPropertyChain sup) {
		super(sub, sub.getFirstProperty(), sup);
	}

	@Override
	public ReflexivePropertyChainImpl<IndexedPropertyChain> getReflexivePremise() {
		return new ReflexivePropertyChainImpl<IndexedPropertyChain>(
				chain.getSuffixChain());
	}

	@Override
	public <I, O> O accept(SubPropertyChainInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
