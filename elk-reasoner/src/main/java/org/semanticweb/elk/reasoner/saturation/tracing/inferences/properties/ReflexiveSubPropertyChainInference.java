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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class ReflexiveSubPropertyChainInference<R extends IndexedPropertyChain> extends SubPropertyChain<R, IndexedPropertyChain> implements ObjectPropertyInference {

	final IndexedBinaryPropertyChain chain;
	
	protected ReflexiveSubPropertyChainInference(IndexedBinaryPropertyChain chain, R subChain, IndexedPropertyChain sup) {
		super(subChain, sup);
		
		this.chain = chain;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj.getClass().equals(getClass()))) {
			return false;
		}
		
		ReflexiveSubPropertyChainInference<?> inf = (ReflexiveSubPropertyChainInference<?>) obj;
		
		return chain.equals(inf.chain) && getSuperPropertyChain().equals(inf.getSuperPropertyChain());
	}

	@Override
	public int hashCode() {
		return HashGenerator.combineListHash(chain.hashCode(), getSuperPropertyChain().hashCode());
	}	
	
	public IndexedBinaryPropertyChain getPremiseChain() {
		return chain;
	}
	
	public abstract ReflexivePropertyChain<?> getReflexivePremise();

}
