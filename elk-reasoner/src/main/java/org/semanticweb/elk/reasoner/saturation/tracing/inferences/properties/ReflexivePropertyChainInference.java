/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.InferencePrinter;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Represents an inference that a property chain is reflexive if it is composed of reflexive property sub-chains.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexivePropertyChainInference extends ReflexivePropertyChain<IndexedBinaryPropertyChain> implements ObjectPropertyInference {

	public ReflexivePropertyChainInference(IndexedBinaryPropertyChain chain) {
		super(chain);
	}
	
	public PropertyChainInitialization getPropertyChainInitialization() {
		return new PropertyChainInitialization(getPropertyChain());
	}
	
	public ReflexivePropertyChain<IndexedObjectProperty> getLeftReflexiveProperty() {
		return new ReflexivePropertyChain<IndexedObjectProperty>(getPropertyChain().getLeftProperty());
	}
	
	public ReflexivePropertyChain<?> getRightReflexivePropertyChain() {
		return new ReflexivePropertyChain<IndexedPropertyChain>(getPropertyChain().getRightProperty());
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public String toString() {
		return new InferencePrinter().visit(this, null);
	}
	
}
