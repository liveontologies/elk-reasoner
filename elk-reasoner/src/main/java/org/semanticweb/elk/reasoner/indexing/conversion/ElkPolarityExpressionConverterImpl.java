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
package org.semanticweb.elk.reasoner.indexing.conversion;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.predefined.ElkPolarity;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.completeness.OccurrenceListener;
import org.semanticweb.elk.reasoner.indexing.classes.ModifiableIndexedObjectBaseFactory;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.classes.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;

/**
 * 
 * An implementation of {@link ElkPolarityExpressionConverter} that converts
 * that creates {@link ModifiableIndexedObject}s using a provided
 * {@link ModifiableIndexedObject.Factory}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ElkPolarityExpressionConverterImpl
		implements ElkPolarityExpressionConverter {

	private final ElkPolarityExpressionConverter complementaryConverter_;

	private final PredefinedElkClassFactory elkFactory_;

	private final ModifiableIndexedObject.Factory factory_;

	private final int increment_;

	private final OccurrenceListener occurrenceListener_;

	private final ElkPolarity polarity_;

	ElkPolarityExpressionConverterImpl(ElkPolarity polarity,
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObject.Factory factory,
			ElkPolarityExpressionConverter complementaryConverter,
			final OccurrenceListener occurrenceListener, int increment) {
		this.polarity_ = polarity;
		this.elkFactory_ = elkFactory;
		this.factory_ = factory;
		this.complementaryConverter_ = complementaryConverter;
		this.occurrenceListener_ = occurrenceListener;
		this.increment_ = increment;
	}

	/**
	 * Creates an {@link ElkPolarityExpressionConverter} of the given
	 * {@link ElkPolarity} using two {@link ModifiableIndexedObject.Factory}s
	 * for creating {@link ModifiableIndexedObject}s of the given polarity and
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
	 * @param increment
	 *            indicates whether the converted expressions must be inserted
	 *            (> 0) or deleted (< 0) and with which multiplicity
	 */
	public ElkPolarityExpressionConverterImpl(ElkPolarity polarity,
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObject.Factory factory,
			ModifiableIndexedObject.Factory complementaryFactory,
			final OccurrenceListener occurrenceTracker, int increment) {
		this.polarity_ = polarity;
		this.elkFactory_ = elkFactory;
		this.factory_ = factory;
		this.complementaryConverter_ = new ElkPolarityExpressionConverterImpl(
				polarity.getComplementary(), elkFactory, complementaryFactory,
				this, occurrenceTracker, increment);
		this.occurrenceListener_ = occurrenceTracker;
		this.increment_ = increment;
	}

	/**
	 * Creates an {@link ElkPolarityExpressionConverter} of polarity
	 * {@link ElkPolarity#DUAL}, which uses the given
	 * {@link ModifiableIndexedObject.Factory} for creating the
	 * {@link ModifiableIndexedObject}s (of the dual polarity). This converter
	 * returns itself as the complementary converter.
	 * 
	 * @param dualFactory
	 *            {@link ModifiableIndexedObject.Factory} used for creating the
	 *            {@link ModifiableIndexedObject}s of this converter
	 * 
	 */
	public ElkPolarityExpressionConverterImpl(
			PredefinedElkClassFactory elkFactory,
			ModifiableIndexedObject.Factory dualFactory,
			final OccurrenceListener occurrrenceTracker, int increment) {
		this.polarity_ = ElkPolarity.DUAL;
		this.elkFactory_ = elkFactory;
		this.factory_ = dualFactory;
		this.complementaryConverter_ = this;
		this.occurrenceListener_ = occurrrenceTracker;
		this.increment_ = increment;
	}

	/**
	 * Creates an {@link ElkPolarityExpressionConverter} of polarity
	 * {@link ElkPolarity#DUAL}, which uses
	 * {@link ResolvingModifiableIndexedObjectFactory} for creating the
	 * {@link ModifiableIndexedObject}s.
	 * 
	 * @param elkFactory
	 * @param index
	 */
	public ElkPolarityExpressionConverterImpl(
			PredefinedElkClassFactory elkFactory,
			ModifiableOntologyIndex index) {
		this(elkFactory, new ResolvingModifiableIndexedObjectFactory(index),
				index, 0);
	}

	/**
	 * Creates an {@link ElkPolarityExpressionConverter} of polarity
	 * {@link ElkPolarity#DUAL}, which uses
	 * {@link UpdatingModifiableIndexedObjectFactory} that increments the number
	 * of occurrences of the created {@link ModifiableIndexedObject}s by the
	 * specified increment. This converter returns itself as the complementary
	 * converter.
	 * 
	 * @param elkFactory
	 * @param index
	 * @param increment
	 */
	public ElkPolarityExpressionConverterImpl(
			final PredefinedElkClassFactory elkFactory,
			final ModifiableOntologyIndex index, final int increment) {
		this(elkFactory,
				new UpdatingModifiableIndexedObjectFactory(
						new ModifiableIndexedObjectBaseFactory(), index,
						OccurrenceIncrement.getDualIncrement(increment)),
				index, increment);
	}

	@Override
	public ElkPolarityExpressionConverter getComplementaryConverter() {
		return this.complementaryConverter_;
	}

	@Override
	public ElkPolarity getPolarity() {
		return polarity_;
	}

	private ElkIndexingUnsupportedFeature unsupported(Feature feature) {
		return new ElkIndexingUnsupportedFeature(feature);
	}

	@Override
	public ModifiableIndexedIndividual visit(
			ElkAnonymousIndividual expression) {
		throw unsupported(Feature.ANONYMOUS_INDIVIDUAL);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkClass elkClass) {
		return factory_.getIndexedClass(elkClass);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataAllValuesFrom expression) {
		throw unsupported(Feature.DATA_ALL_VALUES_FROM);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataExactCardinalityQualified expression) {
		throw unsupported(Feature.DATA_EXACT_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataExactCardinalityUnqualified expression) {
		throw unsupported(Feature.DATA_EXACT_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataHasValue elkDataHasValue) {
		occurrenceListener_.occurrenceChanged(
				Feature.DATA_HAS_VALUE, increment_);
		return factory_.getIndexedDataHasValue(elkDataHasValue);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMaxCardinalityQualified expression) {
		throw unsupported(Feature.DATA_MAX_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMaxCardinalityUnqualified expression) {
		throw unsupported(Feature.DATA_MAX_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMinCardinalityQualified expression) {
		throw unsupported(Feature.DATA_MIN_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMinCardinalityUnqualified expression) {
		throw unsupported(Feature.DATA_MIN_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataSomeValuesFrom expression) {
		throw unsupported(Feature.DATA_SOME_VALUES_FROM);
	}

	@Override
	public ModifiableIndexedIndividual visit(
			ElkNamedIndividual elkNamedIndividual) {
		return factory_.getIndexedIndividual(elkNamedIndividual);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectAllValuesFrom expression) {
		throw unsupported(Feature.OBJECT_ALL_VALUES_FROM);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectComplementOf elkObjectComplementOf) {
		return factory_.getIndexedObjectComplementOf(elkObjectComplementOf
				.getClassExpression().accept(complementaryConverter_));
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectExactCardinalityQualified expression) {
		throw unsupported(Feature.OBJECT_EXACT_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectExactCardinalityUnqualified expression) {
		throw unsupported(Feature.OBJECT_EXACT_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectHasSelf expression) {
		throw unsupported(Feature.OBJECT_HAS_SELF);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectHasValue elkObjectHasValue) {
		return factory_.getIndexedObjectSomeValuesFrom(
				elkObjectHasValue.getProperty().accept(this),
				elkObjectHasValue.getFiller().accept(this));
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
	public ModifiableIndexedObjectProperty visit(
			ElkObjectInverseOf expression) {
		throw unsupported(Feature.OBJECT_INVERSE_OF);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified expression) {
		throw unsupported(Feature.OBJECT_MAX_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMaxCardinalityUnqualified expression) {
		throw unsupported(Feature.OBJECT_MAX_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMinCardinalityQualified expression) {
		throw unsupported(Feature.OBJECT_MIN_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMinCardinalityUnqualified expression) {
		throw unsupported(Feature.OBJECT_MIN_CARDINALITY);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectOneOf elkObjectOneOf) {
		int size = elkObjectOneOf.getIndividuals().size();
		if (size > 0) {
			occurrenceListener_.occurrenceChanged(Feature.OBJECT_ONE_OF,
					increment_);
		}
		switch (size) {
		case 0:
			return factory_.getIndexedClass(elkFactory_.getOwlNothing());
		case 1:
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
	public ModifiableIndexedObjectProperty visit(
			ElkObjectProperty elkObjectProperty) {
		return factory_.getIndexedObjectProperty(elkObjectProperty);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectSomeValuesFrom elkObjectSomeValuesFrom) {
		return factory_.getIndexedObjectSomeValuesFrom(
				elkObjectSomeValuesFrom.getProperty().accept(this),
				elkObjectSomeValuesFrom.getFiller().accept(this));
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

}
