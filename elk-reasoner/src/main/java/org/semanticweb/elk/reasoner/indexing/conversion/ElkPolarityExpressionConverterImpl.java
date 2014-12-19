package org.semanticweb.elk.reasoner.indexing.conversion;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.factories.ModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElkPolarityExpressionConverterImpl extends
		FailingElkPolarityExpressionConverter {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ElkPolarityExpressionConverterImpl.class);

	private final ModifiableIndexedObjectFactory factory_;

	private final ElkPolarityExpressionConverter complementaryConverter_;

	public ElkPolarityExpressionConverterImpl(
			ModifiableIndexedObjectFactory factory,
			ElkPolarityExpressionConverter complementaryConverter) {
		this.factory_ = factory;
		this.complementaryConverter_ = complementaryConverter;
	}

	public ElkPolarityExpressionConverterImpl(
			ModifiableIndexedObjectFactory factory,
			ModifiableIndexedObjectFactory complementaryFactory) {
		this.factory_ = factory;
		this.complementaryConverter_ = new ElkPolarityExpressionConverterImpl(
				complementaryFactory, this);
	}

	public ElkPolarityExpressionConverterImpl(
			ModifiableIndexedObjectFactory dualFactory) {
		this.factory_ = dualFactory;
		this.complementaryConverter_ = this;
	}

	@Override
	public ElkPolarityExpressionConverter getComplementaryConverter() {
		return this.complementaryConverter_;
	}

	@Override
	public ModifiableIndexedIndividual visit(
			ElkNamedIndividual elkNamedIndividual) {
		return factory_.getIndexedIndividual(elkNamedIndividual);
	}

	@Override
	public ModifiableIndexedObjectProperty visit(
			ElkObjectProperty elkObjectProperty) {
		return factory_.getIndexedObjectProperty(elkObjectProperty);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkClass elkClass) {
		return factory_.getIndexedClass(elkClass);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectHasValue elkObjectHasValue) {
		return factory_.getIndexedObjectSomeValuesFrom(elkObjectHasValue
				.getProperty().accept(this), elkObjectHasValue.getFiller()
				.accept(this));
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		return factory_.getIndexedObjectComplementOf(elkObjectComplementOf
				.getClassExpression().accept(complementaryConverter_));
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectIntersectionOf elkObjectIntersectionOf) {
		// binarization
		ModifiableIndexedClassExpression result = null;
		for (ElkClassExpression conjunct : elkObjectIntersectionOf
				.getClassExpressions()) {
			ModifiableIndexedClassExpression ice = conjunct.accept(this);
			if (result == null)
				result = ice;
			else
				result = factory_.getIndexedObjectIntersectionOf(result, ice);
		}
		return result;
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return factory_.getIndexedObjectSomeValuesFrom(elkObjectSomeValuesFrom
				.getProperty().accept(this), elkObjectSomeValuesFrom
				.getFiller().accept(this));
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectUnionOf elkObjectUnionOf) {
		// TODO: handle a special case of singleton ObjectOneOf
		List<ModifiableIndexedClassExpression> disjuncts = new ArrayList<ModifiableIndexedClassExpression>(
				elkObjectUnionOf.getClassExpressions().size());
		for (ElkClassExpression conjunct : elkObjectUnionOf
				.getClassExpressions()) {
			disjuncts.add(conjunct.accept(this));
		}
		return factory_.getIndexedObjectUnionOf(disjuncts);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataHasValue elkDataHasValue) {
		if (LOGGER_.isWarnEnabled()) {
			LoggerWrap
					.log(LOGGER_, LogLevel.WARN,
							"reasoner.indexing.dataHasValue",
							"ELK supports DataHasValue only partially. Reasoning might be incomplete!");
		}
		return factory_.getIndexedDataHasValue(elkDataHasValue);
	}

}
