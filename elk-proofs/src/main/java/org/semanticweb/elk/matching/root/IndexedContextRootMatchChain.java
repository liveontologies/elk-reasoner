package org.semanticweb.elk.matching.root;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

public class IndexedContextRootMatchChain {

	private final IndexedContextRootMatch head_;

	private final IndexedContextRootMatchChain tail_;

	public IndexedContextRootMatchChain(IndexedContextRootMatch head,
			IndexedContextRootMatchChain tail) {
		this.head_ = head;
		this.tail_ = tail;
	}

	public IndexedContextRootMatch getHead() {
		return head_;
	}

	public IndexedContextRootMatchChain getTail() {
		return tail_;
	}

}
