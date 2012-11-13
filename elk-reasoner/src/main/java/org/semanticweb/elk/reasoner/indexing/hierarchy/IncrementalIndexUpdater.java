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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleChain;

/**
 * An index updater that saves the changes into the {@link DifferentialIndex} object,
 * instead of immediately applying them for the affected indexed objects. The
 * changes can be committed to the indexed object all at once by calling the
 * respective method.
 * 
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 * 
 */
public class IncrementalIndexUpdater implements IndexUpdater {

	private final DifferentialIndex differentialIndex_;

	public IncrementalIndexUpdater(DifferentialIndex indexChange_) {
		this.differentialIndex_ = indexChange_;
	}

	@Override
	public void addClass(ElkClass newClass) {
		differentialIndex_.addedClasses.add(newClass);
	}

	@Override
	public void removeClass(ElkClass oldClass) {
		differentialIndex_.removedClasses.add(oldClass);
	}
	
	@Override
	public void addNamedIndividual(ElkNamedIndividual newIndividual) {
		differentialIndex_.addedIndividuals.add(newIndividual);
	}

	@Override
	public void removeNamedIndividual(ElkNamedIndividual newIndividual) {
		differentialIndex_.removedIndividuals.remove(newIndividual);
	}

	@Override
	public boolean add(IndexedClassExpression target, RuleChain<Context> rules) {
		return differentialIndex_.registerContextRuleAdditions(target, rules);
	}

	@Override
	public boolean remove(IndexedClassExpression target, RuleChain<Context> rules) {
		return differentialIndex_.registerContextRuleDeletions(target, rules);
	}

	@Override
	public boolean add(IndexedClassExpression target,	IndexRules<IndexedClassExpression> rules) {
		return differentialIndex_.registerIndexRuleAdditions(target, rules);
	}

	@Override
	public boolean remove(IndexedClassExpression target, IndexRules<IndexedClassExpression> rules) {
		return differentialIndex_.registerIndexRuleDeletions(target, rules);
	}

	@Override
	public boolean add(RuleChain<Context> rules) {
		return differentialIndex_.registerContextInitRuleAdditions(rules);
	}

	@Override
	public boolean remove(RuleChain<Context> rules) {
		return differentialIndex_.registerContextInitRuleDeletions(rules);
	}
}