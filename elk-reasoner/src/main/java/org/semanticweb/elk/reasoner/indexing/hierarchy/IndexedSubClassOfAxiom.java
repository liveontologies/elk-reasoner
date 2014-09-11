package org.semanticweb.elk.reasoner.indexing.hierarchy;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleToIndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedSubClassOfAxiom.class);

	private final IndexedClassExpression subClass_, superClass_;
	/*
	 * this shouldn't cause much memory overhead because we don't cache indexed subclass axioms. 
	 */
	private final ElkAxiom assertedAxiom_;

	protected IndexedSubClassOfAxiom(IndexedClassExpression subClass,
			IndexedClassExpression superClass, ElkAxiom assertedAxiom) {
		this.subClass_ = subClass;
		this.superClass_ = superClass;
		this.assertedAxiom_ = assertedAxiom;
	}

	public IndexedClassExpression getSubClass() {
		return this.subClass_;
	}

	public IndexedClassExpression getSuperClass() {
		return this.superClass_;
	}

	@Override
	public boolean occurs() {
		// we do not cache sub class axioms
		// TODO: introduce a method for testing if we cache an object in the
		// index
		return false;
	}

	@Override
	public String toStringStructural() {
		return "SubClassOf(" + this.subClass_ + ' ' + this.superClass_ + ')';
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	protected void updateOccurrenceNumbers(final ModifiableOntologyIndex index, final RuleToIndexWriter ruleWriter,
			final int increment) {
		if (increment > 0) {
			//SuperClassFromSubClassRule.addRuleFor(this, index);
			ruleWriter.addSuperClassFromSubClassRule(this, index, assertedAxiom_);
		} else {
			//SuperClassFromSubClassRule.removeRuleFor(this, index);
			ruleWriter.removeSuperClassFromSubClassRule(this, index);
		}
	}

}
