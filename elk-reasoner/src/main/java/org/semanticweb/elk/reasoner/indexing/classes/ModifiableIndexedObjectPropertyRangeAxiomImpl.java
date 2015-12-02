package org.semanticweb.elk.reasoner.indexing.classes;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;

/**
 * Implements {@link ModifiableIndexedObjectPropertyRangeAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 */
class ModifiableIndexedObjectPropertyRangeAxiomImpl<A extends ElkAxiom> extends
		ModifiableIndexedAxiomImpl<A> implements
		ModifiableIndexedObjectPropertyRangeAxiom {

	private final ModifiableIndexedObjectProperty property_;

	private final ModifiableIndexedClassExpression range_;

	ModifiableIndexedObjectPropertyRangeAxiomImpl(
			A originalAxiom,
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		super(originalAxiom);
		this.property_ = property;
		this.range_ = range;
	}

	@Override
	public final ModifiableIndexedObjectProperty getProperty() {
		return this.property_;
	}

	@Override
	public final ModifiableIndexedClassExpression getRange() {
		return this.range_;
	}

	@Override
	public final String toStringStructural() {
		return "ObjectPropertyRange(" + this.property_ + ' ' + this.range_
				+ ')';
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {		
		return property_.addToldRange(range_, getOriginalAxiom());
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		return property_.removeToldRange(range_, getOriginalAxiom());
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}