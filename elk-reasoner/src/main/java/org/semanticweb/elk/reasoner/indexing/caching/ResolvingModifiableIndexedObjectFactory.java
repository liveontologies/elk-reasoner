package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObjectPropertyOfAxiom;

public class ResolvingModifiableIndexedObjectFactory extends
		ResolvingCachedIndexedObjectFactory implements
		ModifiableIndexedObjectFactory {

	private final ModifiableIndexedObjectFactory baseFactory_;

	public <F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> ResolvingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableIndexedObjectCache cache) {
		super(baseFactory, cache);
		this.baseFactory_ = baseFactory;
	}

	@SuppressWarnings("static-method")
	<T extends ModifiableIndexedAxiom> T filter(T input) {
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

}
