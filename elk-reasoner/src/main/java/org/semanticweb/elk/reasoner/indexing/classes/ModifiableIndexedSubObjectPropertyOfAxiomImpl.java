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
import org.semanticweb.elk.reasoner.indexing.conversion.ElkUnexpectedIndexingException;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;

/**
 * Implements {@link ModifiableIndexedSubObjectPropertyOfAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 */
class ModifiableIndexedSubObjectPropertyOfAxiomImpl<A extends ElkAxiom>
		extends
			IndexedSubObjectPropertyOfAxiomImpl<A, ModifiableIndexedPropertyChain, ModifiableIndexedObjectProperty>
		implements
			ModifiableIndexedSubObjectPropertyOfAxiom {

	ModifiableIndexedSubObjectPropertyOfAxiomImpl(A originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		super(originalAxiom, subPropertyChain, superProperty);
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {
		ElkAxiom reason = getOriginalAxiom();
		ModifiableIndexedPropertyChain subPropertyChain = getSubPropertyChain();
		ModifiableIndexedObjectProperty superProperty = getSuperProperty();
		if (!subPropertyChain.addToldSuperObjectProperty(superProperty, reason))
			return false;
		if (!superProperty.addToldSubPropertyChain(subPropertyChain, reason)) {
			// revert the changes
			if (!subPropertyChain.removeToldSuperObjectProperty(superProperty,
					reason))
				throw new ElkUnexpectedIndexingException(this);
			return false;
		}
		// success
		return true;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		ElkAxiom reason = getOriginalAxiom();
		ModifiableIndexedPropertyChain subPropertyChain = getSubPropertyChain();
		ModifiableIndexedObjectProperty superProperty = getSuperProperty();
		if (!subPropertyChain.removeToldSuperObjectProperty(superProperty,
				reason))
			return false;
		if (!superProperty.removeToldSubPropertyChain(subPropertyChain,
				reason)) {
			// revert the changes
			if (!subPropertyChain.addToldSuperObjectProperty(superProperty,
					reason))
				throw new ElkUnexpectedIndexingException(this);
			return false;
		}
		// success
		return true;
	}

}
