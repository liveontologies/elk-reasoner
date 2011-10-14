/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.views.IndexedClassExpressionView;
import org.semanticweb.elk.reasoner.indexing.views.IndexedPropertyChainView;
import org.semanticweb.elk.reasoner.indexing.views.IndexedViewConverter;

public class IndexedObjectCache implements IndexedObjectFilter {

	protected final Map<IndexedClassExpressionView<? extends IndexedClassExpression>, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<IndexedPropertyChainView<? extends IndexedPropertyChain>, IndexedPropertyChain> indexedPropertyChainLookup;
	protected int indexedClassCount = 0;
	protected int indexedObjectPropertyCount = 0;
	
	protected IndexedObjectCache() {
		indexedClassExpressionLookup = 
			new HashMap<IndexedClassExpressionView<? extends IndexedClassExpression>, IndexedClassExpression>(
				1023);
		indexedPropertyChainLookup = 
			new HashMap<IndexedPropertyChainView<? extends IndexedPropertyChain>, IndexedPropertyChain>(
				127);
	}

	public IndexedClassExpression filter(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		IndexedClassExpression result = indexedClassExpressionLookup
				.get(iceView);
		if (result == null)
			return ice;
		else
			return result;
	}

	public IndexedPropertyChain filter(IndexedPropertyChain ipc) {
		IndexedPropertyChainView<? extends IndexedPropertyChain> ipcView = ipc
				.accept(IndexedViewConverter.getInstance());
		IndexedPropertyChain result = indexedPropertyChainLookup
				.get(ipcView);
		if (result == null)
			return ipc;
		else
			return result;
	}

	protected void add(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		indexedClassExpressionLookup.put(iceView, ice);
		if (ice instanceof IndexedClass)
			indexedClassCount++;
	}

	protected void add(IndexedPropertyChain ipc) {
		IndexedPropertyChainView<? extends IndexedPropertyChain> ipcView = ipc
				.accept(IndexedViewConverter.getInstance());
		indexedPropertyChainLookup.put(ipcView, ipc);
		if (ipc instanceof IndexedObjectProperty)
			indexedObjectPropertyCount++;
	}

	protected void remove(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		indexedClassExpressionLookup.remove(iceView);
		if (ice instanceof IndexedClass)
			indexedClassCount--;
	}

	protected void remove(IndexedPropertyChain ipc) {
		IndexedPropertyChainView<? extends IndexedPropertyChain> ipcView = ipc
				.accept(IndexedViewConverter.getInstance());
		indexedPropertyChainLookup.remove(ipcView);
		if (ipc instanceof IndexedObjectProperty)
			indexedObjectPropertyCount--;
	}
}
