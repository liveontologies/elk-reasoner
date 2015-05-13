package org.semanticweb.elk.reasoner.indexing.caching;

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

import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;

/**
 * A {@link ModifiableIndexedObjectFactory} that constructs objects using
 * another {@link ModifiableIndexedObjectFactory} and updates the occurrence
 * counts for the constructed objects using the provided
 * {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ModifiableIndexedObject#updateOccurrenceNumbers
 *
 */
public class UpdatingModifiableIndexedObjectFactory extends
		UpdatingCachedIndexedObjectFactory implements
		ModifiableIndexedObjectFactory {

	private final ModifiableIndexedObjectFactory baseFactory_;

	private final ModifiableOntologyIndex index_;

	private final OccurrenceIncrement increment_;

	public <F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> UpdatingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		super(baseFactory, index, increment);
		this.baseFactory_ = baseFactory;
		this.index_ = index;
		this.increment_ = increment;
	}

	<T extends ModifiableIndexedAxiom> T filter(T input) {
		input.updateOccurrenceNumbers(index_, increment_);
		return input;
	}

	@Override
	public final ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ModifiableIndexedEntity entity) {
		return filter(baseFactory_.getIndexedDeclarationAxiom(entity));
	}

	@Override
	public final ModifiableIndexedReflexiveObjectPropertyAxiom getIndexedReflexiveObjectPropertyAxiom(
			ModifiableIndexedObjectProperty property) {
		return filter(baseFactory_
				.getIndexedReflexiveObjectPropertyAxiom(property));
	}

	@Override
	public final ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return filter(baseFactory_.getIndexedSubClassOfAxiom(subClass,
				superClass));
	}

	@Override
	public final ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return filter(baseFactory_.getIndexedSubObjectPropertyOfAxiom(
				subPropertyChain, superProperty));
	}

	@Override
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return filter(baseFactory_.getIndexedObjectPropertyRangeAxiom(property,
				range));
	}

}
