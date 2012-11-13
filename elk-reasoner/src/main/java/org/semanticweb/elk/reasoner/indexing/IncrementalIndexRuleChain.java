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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.chains.AbstractChain;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalIndexRuleChain extends
		AbstractChain<IndexRuleChain<IndexedClassExpression>> {

	// private static final Logger LOGGER_ =
	// Logger.getLogger(IncrementalIndexRuleChain.class);

	private IndexRuleChain<IndexedClassExpression> indexRules_ = null;

	@Override
	public IndexRuleChain<IndexedClassExpression> next() {
		return indexRules_;
	}

	@Override
	public void setNext(IndexRuleChain<IndexedClassExpression> tail) {
		indexRules_ = tail;
	}

	public void apply(IndexedClassExpression target) {
		IndexRuleChain<IndexedClassExpression> next = indexRules_;

		while (next != null) {
			next.apply(target);
			next = next.next();
		}
	}

	public void deapply(IndexedClassExpression target) {
		IndexRuleChain<IndexedClassExpression> next = indexRules_;

		while (next != null) {
			next.deapply(target);
			next = next.next();
		}
	}
}
