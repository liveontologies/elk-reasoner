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
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;

public class UpdatingModifiableIndexedObjectFactory extends
		UpdatingCachedIndexedObjectFactory implements
		ModifiableIndexedObjectFactory {

	private final ModifiableIndexedObjectFactory baseFactory_;

	private final ModifiableOntologyIndex index_;

	private final int increment_;

	private final int positiveIncrement_;

	private final int negativeIncrement_;

	public <F extends CachedIndexedObjectFactory & ModifiableIndexedObjectFactory> UpdatingModifiableIndexedObjectFactory(
			F baseFactory, ModifiableOntologyIndex index, int increment,
			int positiveIncerement, int negativeIncrement) {
		super(baseFactory, index, increment, positiveIncerement,
				negativeIncrement);
		this.baseFactory_ = baseFactory;
		this.index_ = index;
		this.positiveIncrement_ = positiveIncerement;
		this.negativeIncrement_ = negativeIncrement;
		this.increment_ = increment;
	}

	<T extends ModifiableIndexedAxiom> T filter(T input) {
		input.updateOccurrenceNumbers(index_, increment_, positiveIncrement_,
				negativeIncrement_);
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
