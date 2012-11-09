/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.ChainImpl;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class IndexRules<T> extends ChainImpl<IndexRules<T>> implements IndexRule<T, Boolean> {

	protected IndexRules(IndexRules<T> tail) {
		super(tail);
	}
	
/*	@Override
	public Boolean apply(T target) {
		IndexRule<T, Boolean> next = null;
		boolean changed = false;
		
		while((next = next()) != null) {
			changed |= next.apply(target).booleanValue();
		}
		
		return changed;
	}
	
	@Override
	public Boolean deapply(T target) {
		IndexRule<T, Boolean> next = null;
		boolean changed = false;
		
		while((next = next()) != null) {
			changed |= next.deapply(target).booleanValue();
		}
		
		return changed;
	}*/	
	
	/**
	 * Adds these rules to the given chain
	 * 
	 * @param ruleChain
	 * @return
	 */
	public abstract boolean addTo(Chain<IndexRules<T>> ruleChain);
	
	/**
	 * Removes these rules from the given chain
	 * 
	 * @param ruleChain
	 * @return
	 */
	public abstract boolean removeFrom(Chain<IndexRules<T>> ruleChain);	
}
