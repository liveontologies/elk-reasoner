package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * A {@link ModifiableIndexedObject.Factory} that constructs objects using
 * another {@link ModifiableIndexedObject.Factory} and updates the occurrence
 * counts for the constructed objects using the provided
 * {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class UpdatingModifiableIndexedObjectFactory
		extends UpdatingCachedIndexedObjectFactory
		implements ModifiableIndexedObject.Factory {

	private final ModifiableIndexedObject.Factory baseFactory_;

	public <F extends CachedIndexedObject.Factory & ModifiableIndexedObject.Factory> UpdatingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableOntologyIndex index,
			OccurrenceIncrement increment) {
		super(baseFactory, index, increment);
		this.baseFactory_ = baseFactory;
	}

	@Override
	public ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedEntity entity) {
		return update(
				baseFactory_.getIndexedDeclarationAxiom(originalAxiom, entity));
	}

	@Override
	public ModifiableIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		return update(baseFactory_.getIndexedDisjointClassesAxiom(originalAxiom,
				members));
	}

	@Override
	public ModifiableIndexedEquivalentClassesAxiom getIndexedEquivalentClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		return update(baseFactory_.getIndexedEquivalentClassesAxiom(
				originalAxiom, firstMember, secondMember));
	}

	@Override
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		return update(baseFactory_.getIndexedObjectPropertyRangeAxiom(
				originalAxiom, property, range));
	}

	@Override
	public ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		return update(baseFactory_.getIndexedSubClassOfAxiom(originalAxiom,
				subClass, superClass));
	}

	@Override
	public ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		return update(baseFactory_.getIndexedSubObjectPropertyOfAxiom(
				originalAxiom, subPropertyChain, superProperty));
	}

}
