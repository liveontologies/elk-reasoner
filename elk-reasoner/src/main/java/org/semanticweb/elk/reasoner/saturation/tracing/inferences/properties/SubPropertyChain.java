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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;

/**
 * Represents a conclusion that a chain is a sub-property chain of another chain
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubPropertyChain<R extends IndexedPropertyChain, S extends IndexedPropertyChain> implements ObjectPropertyConclusion {

	private final R chain_;
	
	private final S superProperty_;
	
	public SubPropertyChain(R chain, S sup) {
		chain_ = chain;
		superProperty_ = sup;
	}
	
	public R getSubPropertyChain() {
		return chain_;
	}
	
	public S getSuperPropertyChain() {
		return superProperty_;
	}

	@Override
	public String toString() {
		return "SubPropertyChain(" + chain_ + " " + superProperty_ + ")";
	}

	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
}
