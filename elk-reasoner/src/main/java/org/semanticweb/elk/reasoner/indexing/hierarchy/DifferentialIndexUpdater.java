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
 * An index updater that saves the changes into the {@link DifferentialIndex}
 * object, instead of immediately applying them to {@link OntologyIndex}. The
 * changes can be committed afterwards to the main {@link OntologyIndex}.
 * 
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 * 
 */
public class DifferentialIndexUpdater<I extends DifferentialIndex> extends
		DirectIndexUpdater<I> implements IndexUpdater {

	public DifferentialIndexUpdater(I differentialIndex) {
		super(differentialIndex);
	}

	@Override
	public void addClass(ElkClass newClass) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.addClass(newClass);
		else
			super.addClass(newClass);
	}

	@Override
	public void removeClass(ElkClass oldClass) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.removeClass(oldClass);
		else
			super.removeClass(oldClass);
	}

	@Override
	public void addNamedIndividual(ElkNamedIndividual newIndividual) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.addNamedIndividual(newIndividual);
		else
			super.addNamedIndividual(newIndividual);
	}

	@Override
	public void removeNamedIndividual(ElkNamedIndividual newIndividual) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.removeNamedIndividual(newIndividual);
		else
			super.removeNamedIndividual(newIndividual);
	}

	@Override
	public void add(IndexedClassExpression target, ChainableRule<Context> rule) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.registerAddedContextRule(target, rule);
		else
			super.add(target, rule);
	}

	@Override
	public void remove(IndexedClassExpression target,
			ChainableRule<Context> rule) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.registerRemovedContextRule(target, rule);
		else
			super.remove(target, rule);
	}

	@Override
	public void add(ChainableRule<Context> rule) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.registerAddedContextInitRule(rule);
		else
			super.add(rule);
	}

	@Override
	public void remove(ChainableRule<Context> rule) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.registerRemovedContextInitRule(rule);
		else
			super.remove(rule);
	}

	@Override
	public void add(IndexedObject object) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.addIndexedObject(object);
		else
			super.add(object);
	}

	@Override
	public void remove(IndexedObject object) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.removeIndexedObject(object);
		else
			super.remove(object);
	}

	@Override
	public void addReflexiveProperty(IndexedObjectProperty property) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.addReflexiveProperty(property);
		else
			super.addReflexiveProperty(property);
	}

	@Override
	public void removeReflexiveProperty(IndexedObjectProperty property) {
		if (ontologyIndex.incrementalMode)
			ontologyIndex.removeReflexiveProperty(property);
		else
			super.removeReflexiveProperty(property);
	}
}