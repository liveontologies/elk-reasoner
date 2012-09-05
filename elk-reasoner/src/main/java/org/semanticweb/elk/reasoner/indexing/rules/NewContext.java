package org.semanticweb.elk.reasoner.indexing.rules;

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

import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.collections.Multimap;

public interface NewContext {

	public IndexedClassExpression getRoot();

	public Set<IndexedClassExpression> getSuperClassExpressions();

	public Multimap<IndexedPropertyChain, NewContext> getBackwardLinksByObjectProperty();

	public Multimap<IndexedPropertyChain, Conclusion> getPropagationsByObjectProperty();

	public boolean addPropagationByObjectProperty(
			IndexedPropertyChain propRelation, Conclusion conclusion);

	public boolean addBackwardLinkByObjectProperty(IndexedPropertyChain first,
			NewContext second);

	public boolean addSuperClassExpressions(IndexedClassExpression expression);

	public Queue<Conclusion> getToDo();

	public boolean tryActivate();

	public boolean tryDeactivate();

	public void setSatisfiable(boolean b);

	public boolean isSatisfiable();

	public boolean isSaturated();

	public void setSaturated();

}
