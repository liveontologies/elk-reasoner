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

class IndexedObjectCanonizer {

	protected final Map<IndexedClassExpressionView<? extends IndexedClassExpression>, IndexedClassExpression> indexedClassExpressionLookup;
	protected final Map<IndexedPropertyChainView<? extends IndexedPropertyChain>, IndexedPropertyChain> indexedPropertyChainLookup;
	protected int indexedClassCount = 0;
	protected int indexedObjectPropertyCount = 0;
	
	IndexedObjectCanonizer() {
		indexedClassExpressionLookup = 
			new HashMap<IndexedClassExpressionView<? extends IndexedClassExpression>, IndexedClassExpression>(
				1023);
		indexedPropertyChainLookup = 
			new HashMap<IndexedPropertyChainView<? extends IndexedPropertyChain>, IndexedPropertyChain>(
				127);
	}

	IndexedClassExpression get(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		IndexedClassExpression result = indexedClassExpressionLookup
				.get(iceView);
		return result;
	}

	IndexedPropertyChain get(IndexedPropertyChain ipc) {
		IndexedPropertyChainView<? extends IndexedPropertyChain> ipcView = ipc
				.accept(IndexedViewConverter.getInstance());
		IndexedPropertyChain result = indexedPropertyChainLookup
				.get(ipcView);
		return result;
	}

	IndexedClassExpression getCreate(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		IndexedClassExpression result = indexedClassExpressionLookup
				.get(iceView);
		if (result == null) {
			result = ice;
			indexedClassExpressionLookup.put(iceView, result);
			if (ice instanceof IndexedClass)
				indexedClassCount++;
		}
		return result;
	}

	IndexedPropertyChain getCreate(IndexedPropertyChain ipc) {
		IndexedPropertyChainView<? extends IndexedPropertyChain> ipcView = ipc
				.accept(IndexedViewConverter.getInstance());
		IndexedPropertyChain result = indexedPropertyChainLookup
				.get(ipcView);
		if (result == null) {
			result = ipc;
			indexedPropertyChainLookup.put(ipcView, result);
			if (ipc instanceof IndexedObjectProperty)
				indexedObjectPropertyCount++;
		}
		return result;
	}

	boolean remove(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		boolean success = indexedClassExpressionLookup.remove(iceView) != null;
		if (success && ice instanceof IndexedClass)
			indexedClassCount--;
		return success;
	}

	boolean remove(IndexedPropertyChain ipc) {
		IndexedPropertyChainView<? extends IndexedPropertyChain> ipcView = ipc
				.accept(IndexedViewConverter.getInstance());
		boolean success = indexedPropertyChainLookup.remove(ipcView) != null;
		if (success && ipc instanceof IndexedObjectProperty)
			indexedObjectPropertyCount--;
		return success;
	}
}
