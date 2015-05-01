package org.semanticweb.elk.reasoner.indexing.implementation;

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
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;

/**
 * Implements {@link ModifiableIndexedSubObjectPropertyOfAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ModifiableIndexedSubObjectPropertyOfAxiomImpl extends
		ModifiableIndexedAxiomImpl implements
		ModifiableIndexedSubObjectPropertyOfAxiom {

	private final ModifiableIndexedPropertyChain subPropertyChain_;

	private final ModifiableIndexedObjectProperty superProperty_;

	ModifiableIndexedSubObjectPropertyOfAxiomImpl(
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		this.subPropertyChain_ = subPropertyChain;
		this.superProperty_ = superProperty;
	}

	@Override
	public final ModifiableIndexedPropertyChain getSubPropertyChain() {
		return this.subPropertyChain_;
	}

	@Override
	public final ModifiableIndexedObjectProperty getSuperProperty() {
		return this.superProperty_;
	}

	@Override
	public final String toStringStructural() {
		return "SubObjectPropertyOf(" + this.subPropertyChain_ + ' '
				+ this.superProperty_ + ')';
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index, ElkAxiom reason) {
		if (!subPropertyChain_.addToldSuperObjectProperty(superProperty_,
				reason))
			return false;
		if (!superProperty_.addToldSubPropertyChain(subPropertyChain_, reason)) {
			// revert the changes
			if (!subPropertyChain_.removeToldSuperObjectProperty(
					superProperty_, reason))
				throw new ElkUnexpectedIndexingException(this);
			return false;
		}
		// success
		return true;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index,
			ElkAxiom reason) {
		if (!subPropertyChain_.removeToldSuperObjectProperty(superProperty_,
				reason))
			return false;
		if (!superProperty_.removeToldSubPropertyChain(subPropertyChain_,
				reason)) {
			// revert the changes
			if (!subPropertyChain_.addToldSuperObjectProperty(superProperty_,
					reason))
				throw new ElkUnexpectedIndexingException(this);
			return false;
		}
		// success
		return true;
	}

	@Override
	public final <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}