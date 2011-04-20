/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner;

import java.util.List;

import org.semanticweb.elk.syntax.ElkClassExpression;

public class Reasoner {
	final protected Indexer indexer = new Indexer();
	final protected Saturator saturator = new Saturator();
	
	public void classify(List<ElkClassExpression> goals) {
		indexer.reduceRoleHierarchy();
		
		for (ElkClassExpression e : goals)
			saturator.saturate(indexer.getConcept(e));
	}
}