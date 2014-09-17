/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules.subsumers;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * An extension of {@link SuperClassFromSubClassRule} with explicit binding to
 * the axiom asserted in the ontology.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SuperClassFromSubClassRuleWithAxiomBinding extends
		SuperClassFromSubClassRule {

	private List<ElkAxiom> toldAxioms_;
	
	private SuperClassFromSubClassRuleWithAxiomBinding(
			ChainableSubsumerRule tail) {
		super(tail);
		toldAxioms_ = new ArrayList<ElkAxiom>(1);
	}

	private SuperClassFromSubClassRuleWithAxiomBinding(
			IndexedClassExpression ice, ElkAxiom assertedAxiom) {
		super(ice);
		
		toldAxioms_ = new ArrayList<ElkAxiom>(Arrays.asList(assertedAxiom));
	}
	
	public static void addRuleFor(IndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom assertedAxiom) {
		index.add(axiom.getSubClass(),
				new SuperClassFromSubClassRuleWithAxiomBinding(axiom.getSuperClass(), assertedAxiom));
	}

	public static void removeRuleFor(IndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		index.remove(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass()));
	}
	
	protected boolean addToldSuperClassExpression(
			IndexedClassExpression superClassExpression, ElkAxiom assertedAxiom) {
		toldAxioms_.add(assertedAxiom);
		
		return super.addToldSuperClassExpression(superClassExpression);
	}

	protected boolean removeToldSuperClassExpression(
			IndexedClassExpression superClassExpression, ElkAxiom assertedAxiom) {
		toldAxioms_.remove(assertedAxiom);
		
		return super.removeToldSuperClassExpression(superClassExpression);
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		SuperClassFromSubClassRuleWithAxiomBinding rule = getCreate(ruleChain);
		boolean changed = false;

		for (int i = 0; i < toldSuperClassExpressions.size(); i++) {
			changed |= rule.addToldSuperClassExpression(toldSuperClassExpressions.get(i), toldAxioms_.get(i));
		}

		return changed;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		SuperClassFromSubClassRuleWithAxiomBinding rule = get(ruleChain);
		boolean changed = false;

		if (rule != null) {
			for (int i = 0; i < toldSuperClassExpressions.size(); i++) {
				changed |= rule.removeToldSuperClassExpression(toldSuperClassExpressions.get(i));
			}

			if (rule.isEmpty()) {
				return removeEmpty(ruleChain, MATCHER_);
			}
		}

		return changed;
	}
	
	public ElkAxiom getAxiomForConclusion(IndexedClassExpression subsumer) {
		for (int i = 0; i < toldSuperClassExpressions.size(); i++) {
			if (subsumer == toldSuperClassExpressions.get(i)) {
				return toldAxioms_.get(i);
			}
		}
		// not found
		return null;
	}
	
	@Override
	protected SuperClassFromSubClassRuleWithAxiomBinding getCreate(Chain<ChainableSubsumerRule> ruleChain) {
		return ruleChain.getCreate(	MATCHER_, FACTORY_);
	}
	
	@Override
	protected SuperClassFromSubClassRuleWithAxiomBinding get(Chain<ChainableSubsumerRule> ruleChain) {
		return ruleChain.find(MATCHER_);
	}

	private static final Matcher<ChainableSubsumerRule, SuperClassFromSubClassRuleWithAxiomBinding> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, SuperClassFromSubClassRuleWithAxiomBinding>(
			SuperClassFromSubClassRuleWithAxiomBinding.class);

	private static final ReferenceFactory<ChainableSubsumerRule, SuperClassFromSubClassRuleWithAxiomBinding> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, SuperClassFromSubClassRuleWithAxiomBinding>() {
		@Override
		public SuperClassFromSubClassRuleWithAxiomBinding create(ChainableSubsumerRule tail) {
			return new SuperClassFromSubClassRuleWithAxiomBinding(tail);
		}
	};	
	
}
