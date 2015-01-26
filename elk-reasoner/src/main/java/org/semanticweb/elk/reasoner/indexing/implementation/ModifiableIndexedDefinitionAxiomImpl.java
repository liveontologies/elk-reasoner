package org.semanticweb.elk.reasoner.indexing.implementation;

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

import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassFromDefinitionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifiableIndexedDefinitionAxiomImpl extends
		ModifiableIndexedAxiomImpl implements ModifiableIndexedDefinitionAxiom {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(ModifiableIndexedDefinitionAxiomImpl.class);

	private final ModifiableIndexedClass definedClass_;
	private final ModifiableIndexedClassExpression definition_;

	protected ModifiableIndexedDefinitionAxiomImpl(
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		this.definedClass_ = definedClass;
		this.definition_ = definition;
	}

	@Override
	public ModifiableIndexedClass getDefinedClass() {
		return this.definedClass_;
	}

	@Override
	public ModifiableIndexedClassExpression getDefinition() {
		return this.definition_;
	}

	@Override
	public String toStringStructural() {
		return "EquivalentClasses(" + this.definedClass_ + ' '
				+ this.definition_ + ')';
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment) {
		boolean success = true;
		if (increment > 0) {
			int added = 0;
			for (int i = 0; i < increment; i++) {
				if (addOnce(index))
					added++;
				else {
					success = false;
					break;
				}
			}
			if (!success) {
				// revert the changes
				for (; added > 0; added--) {
					if (!removeOnce(index))
						throw new ElkUnexpectedIndexingException(this);
				}
			}
		} else {
			// increment <= 0
			int removed = 0;
			for (int i = 0; i < -increment; i++) {
				if (removeOnce(index))
					removed++;
				else {
					success = false;
					break;
				}
			}
			if (!success) {
				// revert the changes
				for (; removed > 0; removed--) {
					if (!addOnce(index))
						throw new ElkUnexpectedIndexingException(this);
				}
			}
		}
		return success;
	}

	boolean addOnce(ModifiableOntologyIndex index) {
		if (IndexedClassDecomposition.tryAddRuleFor(this, index))
			return IndexedClassFromDefinitionRule.addRuleFor(this, index);
		// else
		return SuperClassFromSubClassRule.addRulesFor(this, index);
	}

	boolean removeOnce(ModifiableOntologyIndex index) {
		if (IndexedClassDecomposition.tryRemoveRuleFor(this, index))
			return IndexedClassFromDefinitionRule.removeRuleFor(this, index);
		// else
		return SuperClassFromSubClassRule.removeRulesFor(this, index);
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
