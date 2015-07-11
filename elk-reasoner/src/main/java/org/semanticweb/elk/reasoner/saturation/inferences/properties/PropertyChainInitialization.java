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

/**
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class PropertyChainInitialization
		extends
		AbstractSubPropertyChainInference<IndexedPropertyChain, IndexedPropertyChain> {

	public PropertyChainInitialization(IndexedPropertyChain prop) {
		super(prop, prop);
	}

	public IndexedPropertyChain getPropertyChain() {
		return super.getSubPropertyChain();
	}

	@Override
	public String toString() {
		return "Initialization( " + getPropertyChain() + " )";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PropertyChainInitialization)) {
			return false;
		}

		PropertyChainInitialization inf = (PropertyChainInitialization) obj;

		return getPropertyChain().equals(inf.getPropertyChain());
	}

	@Override
	public int hashCode() {
		return getPropertyChain().hashCode();
	}

	@Override
	public <I, O> O accept(SubPropertyChainInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
