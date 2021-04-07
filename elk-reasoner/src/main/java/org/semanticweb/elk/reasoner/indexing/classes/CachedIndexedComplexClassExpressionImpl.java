package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexClassExpression;

/**
 * Implements {@link CachedIndexedComplexClassExpression}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class CachedIndexedComplexClassExpressionImpl extends
		CachedIndexedClassExpressionImpl<CachedIndexedComplexClassExpression>
		implements CachedIndexedComplexClassExpression {
	
	CachedIndexedComplexClassExpressionImpl(int structuralHash) {
		super(structuralHash);
	}
	
	@Override
	public final CachedIndexedComplexClassExpression accept(CachedIndexedClassExpression.Filter filter) {
		return accept((CachedIndexedComplexClassExpression.Filter) filter);
	}
	
	@Override
	public final <O> O accept(IndexedClassExpression.Visitor<O> visitor) {
		return accept((IndexedComplexClassExpression.Visitor<O>) visitor);
	}

}
