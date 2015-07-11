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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;

/**
 * An implementation of {@link ReflexivePropertyChain}
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class ReflexivePropertyChainImpl<P extends IndexedPropertyChain>
		implements ReflexivePropertyChain<P> {

	private final P propertyChain_;

	public ReflexivePropertyChainImpl(P chain) {
		propertyChain_ = chain;
	}

	@Override
	public P getPropertyChain() {
		return propertyChain_;
	}

	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return propertyChain_.toString();
	}

}
