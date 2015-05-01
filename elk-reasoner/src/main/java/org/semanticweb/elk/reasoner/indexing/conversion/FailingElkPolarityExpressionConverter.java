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

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
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
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;

/**
 * An {@link ElkPolarityExpressionConverter} that always throws
 * {@link ElkIndexingUnsupportedException}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class FailingElkPolarityExpressionConverter implements
		ElkPolarityExpressionConverter {

	private final ElkPolarity polarity_;

	public FailingElkPolarityExpressionConverter(ElkPolarity polarity) {
		this.polarity_ = polarity;
	}

	private static <O> O fail(ElkObject expression) {
		throw new ElkIndexingUnsupportedException(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkClass expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataAllValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataSomeValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectComplementOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectIntersectionOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectOneOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectUnionOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataExactCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMaxCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMinCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectExactCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMaxCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMinCardinalityQualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataExactCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMaxCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkDataMinCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectExactCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMaxCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectMinCardinalityUnqualified expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectHasSelf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkDataHasValue expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectAllValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(ElkObjectHasValue expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedClassExpression visit(
			ElkObjectSomeValuesFrom expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedIndividual visit(ElkAnonymousIndividual expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedIndividual visit(ElkNamedIndividual expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedObjectProperty visit(ElkObjectInverseOf expression) {
		return fail(expression);
	}

	@Override
	public ModifiableIndexedObjectProperty visit(ElkObjectProperty expression) {
		return fail(expression);
	}

	@Override
	public ElkPolarityExpressionConverter getComplementaryConverter() {
		return this;
	}

	@Override
	public ElkPolarity getPolarity() {
		return this.polarity_;
	}

}
