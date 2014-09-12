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

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedSubClassOfAxiom.class);

	private final IndexedClassExpression subClass_, superClass_;
	
	protected IndexedSubClassOfAxiom(IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		this.subClass_ = subClass;
		this.superClass_ = superClass;
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
	protected void updateOccurrenceNumbers(final ModifiableOntologyIndex index, final int increment) {
		if (increment > 0) {
			SuperClassFromSubClassRule.addRuleFor(this, index);
		} else {
			SuperClassFromSubClassRule.removeRuleFor(this, index);
		}
	}

}
