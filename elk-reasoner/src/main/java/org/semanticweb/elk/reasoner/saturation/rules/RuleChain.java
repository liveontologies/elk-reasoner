package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * A chain of the rules that can be applied to elements of a particular type
 * {@link RuleEngine}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <E>
 *            the type of elements to which the rule can be applied
 * 
 * @see ChainableRule
 * 
 */
public interface RuleChain<E> extends ChainableRule<E>, Chain<RuleChain<E>> {

	/**
	 * Applies all rules in this chain to an element within a
	 * {@link SaturationState.Writer}
	 * 
	 * @param saturationEngine
	 *            a {@link SaturationState.Writer} which could be changed as a
	 *            result of this rule's application
	 * @param element
	 *            the element to which the rule is applied
	 */
	public void applyAll(SaturationState.Writer saturationEngine, E element);

	/**
	 * Adds all rules in this chain to the given {@link Chain}
	 * 
	 * @param ruleChain
	 * @return {@code true} if the input {@link Chain} has been modified
	 * 
	 */
	public boolean addAllTo(Chain<RuleChain<E>> chain);

	/**
	 * Removes all rules in this chain from the given {@link Chain}
	 * 
	 * @param ruleChain
	 * @return {@code true} if the input {@link Chain} has been modified
	 */
	public boolean removeAllFrom(Chain<RuleChain<E>> chain);

}
