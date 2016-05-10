package org.semanticweb.elk.matching.inferences;

import java.util.List;

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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomBinaryConversion;

public class ElkDifferentIndividualsAxiomBinaryConversionMatch1 extends
		AbstractInferenceMatch<ElkDifferentIndividualsAxiomBinaryConversion> {

	ElkDifferentIndividualsAxiomBinaryConversionMatch1(
			ElkDifferentIndividualsAxiomBinaryConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		super(parent);
	}

	public IndexedSubClassOfAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		ElkDifferentIndividualsAxiomBinaryConversion parent = getParent();
		ElkDifferentIndividualsAxiom premise = parent.getOriginalAxiom();
		List<? extends ElkIndividual> disjoint = premise.getIndividuals();
		return factory.getIndexedSubClassOfAxiomMatch2(
				factory.getIndexedSubClassOfAxiomMatch1(
						parent.getConclusion(factory)),
				factory.getObjectIntersectionOf(
						factory.getObjectOneOf(disjoint
								.get(parent.getFirstIndividualPosition())),
						factory.getObjectOneOf(disjoint
								.get(parent.getSecondIndividualPosition()))),
				factory.getOwlNothing());
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

		O visit(ElkDifferentIndividualsAxiomBinaryConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkDifferentIndividualsAxiomBinaryConversionMatch1 getElkDifferentIndividualsAxiomBinaryConversionMatch1(
				ElkDifferentIndividualsAxiomBinaryConversion parent,
				IndexedSubClassOfAxiomMatch1 conclusionMatch);

	}

}
