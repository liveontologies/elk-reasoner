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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.IndexRules;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;


/**
 * Functions through which entries for indexed class expressions are updated.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
interface IndexUpdater {
	
	public boolean add(final IndexedClassExpression target, final ContextRules rules);
	
	public boolean remove(final IndexedClassExpression target, final ContextRules rules);
	
	public boolean add(final IndexedClassExpression target, final IndexRules<IndexedClassExpression> rules);
	
	public boolean remove(final IndexedClassExpression target, final IndexRules<IndexedClassExpression> rules);	
	
	public void addClass(ElkClass newClass);

	public void removeClass(ElkClass oldClass);	
	
	public void addNamedIndividual(ElkNamedIndividual newIndividual);

	public void removeNamedIndividual(ElkNamedIndividual newIndividual);	
}
