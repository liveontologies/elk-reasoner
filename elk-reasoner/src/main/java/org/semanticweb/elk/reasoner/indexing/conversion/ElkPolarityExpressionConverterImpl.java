package org.semanticweb.elk.reasoner.indexing.conversion;

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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.predefined.ElkPolarity;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An implementation of {@link ElkPolarityExpressionConverter} that converts
 * that creates {@link ModifiableIndexedObject}s using a provided
 * {@link ModifiableIndexedObject.Factory}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ElkPolarityExpressionConverterImpl extends
		FailingElkPolarityExpressionConverter {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ElkPolarityExpressionConverterImpl.class);

	private final PredefinedElkClassFactory elkFactory_;
	
	private final ModifiableIndexedObject.Factory factory_;

	private final ElkPolarityExpressionConverter complementaryConverter_;

	ElkPolarityExpressionConverterImpl(ElkPolarity polarity,
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObject.Factory factory,
			ElkPolarityExpressionConverter complementaryConverter) {
		super(polarity);
		this.elkFactory_ = elkFactory;
		this.factory_ = factory;
		this.complementaryConverter_ = complementaryConverter;
	}

	/**
	 * Creates an {@link ElkPolarityExpressionConverter} of the given
	 * {@link ElkPolarity} using two {@link ModifiableIndexedObject.Factory}s for
	 * creating {@link ModifiableIndexedObject}s of the given polarity and
	 * complementary polarity respectively.
	 * 
	 * @param polarity
	 *            the {@link ElkPolarity} associated with this
	 *            {@link ElkPolarityExpressionConverter}
	 * @param factory
	 *            the {@link ModifiableIndexedObject.Factory} for creating
	 *            {@link ModifiableIndexedObject}s of the given polarity
	 * @param complementaryConverter
	 *            the {@link ModifiableIndexedObject.Factory} used by the
	 *            complementary converter for creating
	 *            {@link ModifiableIndexedObject}s of the complementary polarity
	 */
	public ElkPolarityExpressionConverterImpl(ElkPolarity polarity,
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObject.Factory factory,
			ModifiableIndexedObject.Factory complementaryFactory) {
		super(polarity);
		this.elkFactory_ = elkFactory;
		this.factory_ = factory;
		this.complementaryConverter_ = new ElkPolarityExpressionConverterImpl(
				polarity.getComplementary(), elkFactory, complementaryFactory,
				this);
	}

	/**
	 * Creates an {@link ElkPolarityExpressionConverter} of polarity
	 * {@link ElkPolarity#DUAL}, which uses the given
	 * {@link ModifiableIndexedObject.Factory} for creating the
	 * {@link ModifiableIndexedObject.Factory}s (of the dual polarity). This
	 * converter returns itself as the complementary converter.
	 * 
	 * @param dualFactory
	 *            {@link ModifiableIndexedObject.Factory} used for creating the
	 *            {@link ModifiableIndexedObject.Factory}s of this converter
	 * 
	 */
	public ElkPolarityExpressionConverterImpl(
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObject.Factory dualFactory) {
		super(ElkPolarity.DUAL);
		this.elkFactory_ = elkFactory;
		this.factory_ = dualFactory;
		this.complementaryConverter_ = this;
	}

	public ElkPolarityExpressionConverterImpl(
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObjectCache cache) {
		this(elkFactory, new ResolvingModifiableIndexedObjectFactory(cache));
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
		int size = elkObjectIntersectionOf.getClassExpressions().size();
		switch (size) {
		case 0:
			return factory_.getIndexedClass(elkFactory_.getOwlThing());
		default:
			// binarization
			ModifiableIndexedClassExpression result = null;
			for (ElkClassExpression conjunct : elkObjectIntersectionOf
					.getClassExpressions()) {
				ModifiableIndexedClassExpression ice = conjunct.accept(this);
				if (result == null)
					result = ice;
				else
					result = factory_.getIndexedObjectIntersectionOf(result,
							ice);
			}
			return result;
		}
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return factory_.getIndexedObjectSomeValuesFrom(elkObjectSomeValuesFrom
				.getProperty().accept(this), elkObjectSomeValuesFrom
				.getFiller().accept(this));
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectOneOf elkObjectOneOf) {
		int size = elkObjectOneOf.getIndividuals().size();
		switch (size) {
		case 0:
			return factory_.getIndexedClass(elkFactory_.getOwlNothing());
		case 1:
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap
						.log(LOGGER_, LogLevel.WARN,
								"reasoner.indexing.objectOneOf",
								"ELK supports ObjectOneOf only partially. Reasoning might be incomplete!");
			}
			return elkObjectOneOf.getIndividuals().iterator().next()
					.accept(this);
		default:
			List<ModifiableIndexedClassExpression> disjuncts = new ArrayList<ModifiableIndexedClassExpression>(
					size);
			for (ElkIndividual member : elkObjectOneOf.getIndividuals()) {
				disjuncts.add(member.accept(this));
			}
			return factory_.getIndexedObjectUnionOf(disjuncts);
		}
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectUnionOf elkObjectUnionOf) {
		int size = elkObjectUnionOf.getClassExpressions().size();
		switch (size) {
		case 0:
			return factory_.getIndexedClass(elkFactory_.getOwlNothing());
		case 1:
			return elkObjectUnionOf.getClassExpressions().iterator().next()
					.accept(this);
		default:
			List<ModifiableIndexedClassExpression> disjuncts = new ArrayList<ModifiableIndexedClassExpression>(
					size);
			for (ElkClassExpression disjunct : elkObjectUnionOf
					.getClassExpressions()) {
				disjuncts.add(disjunct.accept(this));
			}
			return factory_.getIndexedObjectUnionOf(disjuncts);
		}
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
