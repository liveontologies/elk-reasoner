/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitorEx;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitorEx;

public abstract class IndexedClassEntity extends IndexedClassExpression {

	abstract public <O> O accept(IndexedClassEntityVisitor<O> visitor);

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedClassEntityVisitor<O>) visitor);
	}

	abstract public <O, P> O accept(IndexedClassEntityVisitorEx<O, P> visitor,
			P parameter);

	@Override
	public <O, P> O accept(IndexedClassExpressionVisitorEx<O, P> visitor,
			P parameter) {
		return accept((IndexedClassEntityVisitorEx<O, P>) visitor, parameter);
	}

}
