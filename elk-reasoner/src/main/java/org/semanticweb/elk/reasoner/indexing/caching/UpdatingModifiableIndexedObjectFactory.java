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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
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

	public <F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> UpdatingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		super(baseFactory, index, increment);
		this.baseFactory_ = baseFactory;
	}

	@Override
	public final ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return update(baseFactory_.getIndexedDeclarationAxiom(originalAxiom, entity));
	}

	@Override
	public ModifiableIndexedDefinitionAxiom getIndexedDefinitionAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClass definedClass, ModifiableIndexedClassExpression definition) {
		return update(baseFactory_.getIndexedDefinitionAxiom(originalAxiom,
				definedClass, definition));
	}

	@Override
	public ModifiableIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		return update(baseFactory_.getIndexedDisjointClassesAxiom(
				originalAxiom, disjointClasses));
	}

	@Override
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedObjectProperty property, ModifiableIndexedClassExpression range) {
		return update(baseFactory_.getIndexedObjectPropertyRangeAxiom(originalAxiom,
				property, range));
	}

	@Override
	public final ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass, ModifiableIndexedClassExpression superClass) {
		return update(baseFactory_.getIndexedSubClassOfAxiom(originalAxiom,
				subClass, superClass));
	}

	@Override
	public final ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain, ModifiableIndexedObjectProperty superProperty) {
		return update(baseFactory_.getIndexedSubObjectPropertyOfAxiom(
				originalAxiom, subPropertyChain, superProperty));
	}

}
