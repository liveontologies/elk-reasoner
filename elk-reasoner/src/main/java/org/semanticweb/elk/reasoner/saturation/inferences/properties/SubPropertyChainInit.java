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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class SubPropertyChainInit extends AbstractSubPropertyChainInference {

	public SubPropertyChainInit(IndexedPropertyChain chain) {
		super(chain, chain);
	}

	public IndexedPropertyChain getChain() {
		return super.getSubChain();
	}

	@Override
	public String toString() {
		return "Init sub-chain: " + getSubChain() + " => " + getSuperChain();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SubPropertyChainInit)) {
			return false;
		}

		SubPropertyChainInit inf = (SubPropertyChainInit) obj;

		return getChain().equals(inf.getChain());
	}

	@Override
	public int hashCode() {
		return getChain().hashCode();
	}

	@Override
	public <I, O> O accept(SubPropertyChainInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
