package org.semanticweb.elk.reasoner.saturation.context;

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
import org.semanticweb.elk.reasoner.indexing.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.indexing.rules.Chain;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.util.collections.Multimap;

public interface Context {

	/**
	 * @return the root expression of this context
	 */
	public IndexedClassExpression getRoot();

	/**
	 * Get the queue of items scheduled to be processed in this context.
	 * 
	 * @return queue
	 */
	public Queue<Conclusion> getToDo();

	/**
	 * Ensure that the context is active, and return true if the activation
	 * state has been changed from false to true. This method is thread safe:
	 * for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was not active; returns false otherwise
	 */
	public boolean tryActivate();

	/**
	 * Ensure that the context is not active, and return true if the activation
	 * state has been changed from true to false. This method is thread safe:
	 * for two concurrent executions only one succeeds.
	 * 
	 * @return true if the context was active; returns false otherwise
	 */
	public boolean tryDeactivate();

	public boolean isSatisfiable();

	public boolean isSaturated();

	public Set<IndexedClassExpression> getSuperClassExpressions();

	public Multimap<IndexedPropertyChain, Context> getBackwardLinksByObjectProperty();

	public Chain<BackwardLinkRules> getBackwardLinkRules();

	public boolean addBackwardLinkByObjectProperty(
			IndexedPropertyChain relation, Context target);

	public boolean addForwardLinkByObjectProperty(
			IndexedPropertyChain relation, Context target);

	public boolean addSuperClassExpression(IndexedClassExpression expression);

	public void setSatisfiable(boolean b);

	public void setSaturated();

}
