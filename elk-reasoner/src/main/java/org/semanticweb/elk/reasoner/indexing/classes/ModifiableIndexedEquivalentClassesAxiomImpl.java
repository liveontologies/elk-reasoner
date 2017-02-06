package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.EquivalentClassFirstFromSecondRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.EquivalentClassSecondFromFirstRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassDecompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassFromDefinitionRule;

public class ModifiableIndexedEquivalentClassesAxiomImpl<A extends ElkAxiom>
		extends
		IndexedEquivalentClassesAxiomImpl<A, ModifiableIndexedClassExpression>
		implements ModifiableIndexedEquivalentClassesAxiom {

	protected ModifiableIndexedEquivalentClassesAxiomImpl(A originalAxiom,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		super(originalAxiom, firstMember, secondMember);
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {
		ElkAxiom reason = getOriginalAxiom();
		boolean success = IndexedClassDecompositionRule.addRuleFor(this, index,
				reason);
		if (success) {
			success = IndexedClassFromDefinitionRule.addRuleFor(this, index,
					reason);
			if (success) {
				return true;
			}
			// else revert
			IndexedClassDecompositionRule.removeRuleFor(this, index, reason);
		}
		// else
		success = EquivalentClassFirstFromSecondRule.addRuleFor(this, index,
				reason);
		if (success) {
			success = EquivalentClassSecondFromFirstRule.addRuleFor(this, index,
					reason);
			if (success) {
				return true;
			}
			// else revert
			EquivalentClassFirstFromSecondRule.removeRuleFor(this, index,
					reason);
		}
		// all failed
		return false;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		ElkAxiom reason = getOriginalAxiom();
		boolean success = IndexedClassDecompositionRule.removeRuleFor(this,
				index, reason);
		if (success) {
			success = IndexedClassFromDefinitionRule.removeRuleFor(this, index,
					reason);
			if (success) {
				return true;
			}
			// else revert
			IndexedClassDecompositionRule.addRuleFor(this, index, reason);
		}
		// else
		success = EquivalentClassFirstFromSecondRule.removeRuleFor(this, index,
				reason);
		if (success) {
			success = EquivalentClassSecondFromFirstRule.removeRuleFor(this,
					index, reason);
			if (success) {
				return true;
			}
			// else revert
			EquivalentClassFirstFromSecondRule.addRuleFor(this, index, reason);
		}
		// all failed
		return false;

	}
	
	@Override
	public <O> O accept(ModifiableIndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
