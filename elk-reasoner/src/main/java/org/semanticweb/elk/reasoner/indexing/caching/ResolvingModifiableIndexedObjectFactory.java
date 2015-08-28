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
import org.semanticweb.elk.reasoner.indexing.implementation.ModifiableIndexedObjectFactoryImpl;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObjectPropertyOfAxiom;

/**
 * A {@link ModifiableIndexedObjectFactory} which uses a given
 * {@link ModifiableIndexedObjectCache} to reuse the objects previously created
 * by this factory. An object in the {@link ModifiableIndexedObjectCache} is
 * reused (returned by the factory) if it is structurally equivalent to the one
 * being constructed.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ResolvingModifiableIndexedObjectFactory extends
		ResolvingCachedIndexedObjectFactory implements
		ModifiableIndexedObjectFactory {

	private final ModifiableIndexedObjectFactory baseFactory_;

	public <F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> ResolvingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableIndexedObjectCache cache) {
		super(baseFactory, cache);
		this.baseFactory_ = baseFactory;
	}

	public ResolvingModifiableIndexedObjectFactory(
			ModifiableIndexedObjectCache cache) {
		this(new ModifiableIndexedObjectFactoryImpl(), cache);
	}

	@SuppressWarnings("static-method")
	<T extends ModifiableIndexedAxiom> T filter(T input) {
		return input;
	}

	@Override
	public final ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ModifiableIndexedEntity entity, ElkAxiom reason) {
		return filter(baseFactory_.getIndexedDeclarationAxiom(entity, reason));
	}

	@Override
	public final ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass, ElkAxiom reason) {
		return filter(baseFactory_.getIndexedSubClassOfAxiom(subClass,
				superClass, reason));
	}

	@Override
	public final ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty, ElkAxiom reason) {
		return filter(baseFactory_.getIndexedSubObjectPropertyOfAxiom(
				subPropertyChain, superProperty, reason));
	}

	@Override
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range, ElkAxiom reason) {
		return filter(baseFactory_.getIndexedObjectPropertyRangeAxiom(property,
				range, reason));
	}

}
