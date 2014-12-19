package org.semanticweb.elk.reasoner.indexing.conversion;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;

public class FailingElkEntityConverter implements ElkEntityConverter {

	private static <O> O fail(ElkEntity expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkAnnotationProperty expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkClass expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkDataProperty expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkDatatype expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkNamedIndividual expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedEntity visit(ElkObjectProperty expression) {
		return fail(expression);
	}

}
