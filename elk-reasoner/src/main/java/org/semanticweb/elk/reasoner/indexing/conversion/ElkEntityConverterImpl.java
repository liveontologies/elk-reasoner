package org.semanticweb.elk.reasoner.indexing.conversion;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedEntityFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;

class ElkEntityConverterImpl extends FailingElkEntityConverter {

	private final ModifiableIndexedEntityFactory factory_;

	public ElkEntityConverterImpl(ModifiableIndexedEntityFactory factory) {
		this.factory_ = factory;
	}

	@Override
	public ModifiableIndexedEntity visit(ElkClass expression) {
		return factory_.getIndexedClass(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkNamedIndividual expression) {
		return factory_.getIndexedIndividual(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkObjectProperty expression) {
		return factory_.getIndexedObjectProperty(expression);
	}

}
