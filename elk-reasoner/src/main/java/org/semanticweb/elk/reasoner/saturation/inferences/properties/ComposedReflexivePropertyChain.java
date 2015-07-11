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
import org.semanticweb.elk.reasoner.saturation.inferences.util.InferencePrinter;

/**
 * Represents an inference that a property chain is reflexive if it is composed
 * of reflexive property sub-chains.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class ComposedReflexivePropertyChain extends
		AbstractReflexivePropertyChainInference<IndexedComplexPropertyChain> {

	public ComposedReflexivePropertyChain(IndexedComplexPropertyChain chain) {
		super(chain);
	}

	public PropertyChainInitialization getPropertyChainInitialization() {
		return new PropertyChainInitialization(getPropertyChain());
	}

	public ReflexivePropertyChainImpl<IndexedObjectProperty> getLeftReflexiveProperty() {
		return new ReflexivePropertyChainImpl<IndexedObjectProperty>(
				getPropertyChain().getFirstProperty());
	}

	public ReflexivePropertyChainImpl<?> getRightReflexivePropertyChain() {
		return new ReflexivePropertyChainImpl<IndexedPropertyChain>(
				getPropertyChain().getSuffixChain());
	}

	@Override
	public String toString() {
		return new InferencePrinter().visit(this, null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ComposedReflexivePropertyChain)) {
			return false;
		}

		ComposedReflexivePropertyChain inf = (ComposedReflexivePropertyChain) obj;

		return getPropertyChain().equals(inf.getPropertyChain());
	}

	@Override
	public int hashCode() {
		return getPropertyChain().hashCode();
	}

	@Override
	public <I, O> O accept(
			ReflexivePropertyChainInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
}
