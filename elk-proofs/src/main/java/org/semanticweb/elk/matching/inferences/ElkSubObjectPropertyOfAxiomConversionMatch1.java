package org.semanticweb.elk.matching.inferences;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubObjectPropertyOfAxiomConversion;

public class ElkSubObjectPropertyOfAxiomConversionMatch1
		extends AbstractInferenceMatch<ElkSubObjectPropertyOfAxiomConversion> {

	ElkSubObjectPropertyOfAxiomConversionMatch1(
			ElkSubObjectPropertyOfAxiomConversion parent,
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		super(parent);
	}

	public IndexedSubObjectPropertyOfAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		ElkSubObjectPropertyOfAxiomConversion parent = getParent();
		ElkSubObjectPropertyOfAxiom premise = parent.getOriginalAxiom();
		ElkObjectPropertyExpression superPropertyExpression = premise
				.getSuperObjectPropertyExpression();
		if (superPropertyExpression instanceof ElkObjectProperty) {
			return factory.getIndexedSubObjectPropertyOfAxiomMatch2(
					factory.getIndexedSubObjectPropertyOfAxiomMatch1(
							parent.getConclusion(factory)),
					premise.getSubObjectPropertyExpression(),
					(ElkObjectProperty) superPropertyExpression);
		} else {
			throw new ElkMatchException(
					parent.getConclusion(factory).getSuperProperty(),
					superPropertyExpression);
		}
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O> {

		O visit(ElkSubObjectPropertyOfAxiomConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkSubObjectPropertyOfAxiomConversionMatch1 getElkSubObjectPropertyOfAxiomConversionMatch1(
				ElkSubObjectPropertyOfAxiomConversion parent,
				IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch);

	}

}
