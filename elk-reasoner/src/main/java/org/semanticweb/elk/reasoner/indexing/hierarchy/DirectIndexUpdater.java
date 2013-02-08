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
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;

/**
 * An index updater through which the index data structures are modified
 * directly.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DirectIndexUpdater<I extends OntologyIndex> implements
		IndexUpdater {

	final I ontologyIndex;

	public DirectIndexUpdater(I index) {
		ontologyIndex = index;
	}

	@Override
	public void addClass(ElkClass newClass) {
	}

	@Override
	public void removeClass(ElkClass oldClass) {
	}

	@Override
	public void addNamedIndividual(ElkNamedIndividual newIndividual) {
	}

	@Override
	public void removeNamedIndividual(ElkNamedIndividual newIndividual) {
	}

	@Override
	public void add(IndexedClassExpression target, ChainableRule<Context> rule) {
		rule.addTo(target.getCompositionRuleChain());
	}

	@Override
	public void remove(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		if (!rule.removeFrom(target.getCompositionRuleChain()))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove composition rule " + rule.getName()
							+ " for " + target);
	}

	@Override
	public void add(ChainableRule<Context> rule) {
		rule.addTo(ontologyIndex.getContextInitRuleChain());
	}

	@Override
	public void remove(ChainableRule<Context> rule) {
		if (!rule.removeFrom(ontologyIndex.getContextInitRuleChain()))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove context initialization rule "
							+ rule.getName());
	}

	@Override
	public void add(IndexedObject object) {
		object.accept(ontologyIndex.getIndexedObjectCache().inserter);
	}

	@Override
	public void remove(IndexedObject object) {
		if (!object.accept(ontologyIndex.getIndexedObjectCache().deletor))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove indexed object from the cache " + object);
		if (object instanceof IndexedClassExpression) {
			IndexedClassExpression ice = (IndexedClassExpression) object;
			Context context = ice.getContext();
			if (context != null)
				context.removeLinks();
		}
	}

	@Override
	public void addReflexiveProperty(IndexedObjectProperty property) {
		ontologyIndex.addReflexiveProperty(property);
	}

	@Override
	public void removeReflexiveProperty(IndexedObjectProperty property) {
		if (!ontologyIndex.removeReflexiveProperty(property))
			throw new ElkUnexpectedIndexingException(
					"Cannot remove reflexivity of object property " + property);
	}

}