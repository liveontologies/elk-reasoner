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
import org.semanticweb.elk.reasoner.indexing.views.IndexedSubPropertyExpressionView;
import org.semanticweb.elk.reasoner.indexing.views.IndexedViewConverter;

class IndexedObjectCanonizer {

	private final Map<IndexedClassExpressionView<? extends IndexedClassExpression>, IndexedClassExpression> indexedClassExpressionLookup;
	private final Map<IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression>, IndexedSubPropertyExpression> indexedSubPropertyExpressionLookup;

	IndexedObjectCanonizer() {
		indexedClassExpressionLookup = new HashMap<IndexedClassExpressionView<? extends IndexedClassExpression>, IndexedClassExpression>(
				1023);
		indexedSubPropertyExpressionLookup = new HashMap<IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression>, IndexedSubPropertyExpression>(
				127);
	}

	IndexedClassExpression get(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		IndexedClassExpression result = indexedClassExpressionLookup
				.get(iceView);
		return result;
	}

	IndexedSubPropertyExpression get(IndexedSubPropertyExpression ipe) {
		IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> ipeView = ipe
				.accept(IndexedViewConverter.getInstance());
		IndexedSubPropertyExpression result = indexedSubPropertyExpressionLookup
				.get(ipeView);
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
		}
		return result;
	}

	IndexedSubPropertyExpression getCreate(IndexedSubPropertyExpression ipe) {
		IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> ipeView = ipe
				.accept(IndexedViewConverter.getInstance());
		IndexedSubPropertyExpression result = indexedSubPropertyExpressionLookup
				.get(ipeView);
		if (result == null) {
			result = ipe;
			indexedSubPropertyExpressionLookup.put(ipeView, result);
		}
		return result;
	}

	void remove(IndexedClassExpression ice) {
		IndexedClassExpressionView<? extends IndexedClassExpression> iceView = ice
				.accept(IndexedViewConverter.getInstance());
		indexedClassExpressionLookup.remove(iceView);
	}

	void remove(IndexedSubPropertyExpression ipe) {
		IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> ipeView = ipe
				.accept(IndexedViewConverter.getInstance());
		indexedSubPropertyExpressionLookup.remove(ipeView);
	}

}
