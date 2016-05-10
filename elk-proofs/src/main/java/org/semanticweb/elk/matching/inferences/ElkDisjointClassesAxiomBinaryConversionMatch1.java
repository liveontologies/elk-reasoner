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

import java.util.List;

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomBinaryConversion;

public class ElkDisjointClassesAxiomBinaryConversionMatch1 extends
		AbstractInferenceMatch<ElkDisjointClassesAxiomBinaryConversion> {

	ElkDisjointClassesAxiomBinaryConversionMatch1(
			ElkDisjointClassesAxiomBinaryConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		super(parent);
	}

	public IndexedSubClassOfAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		ElkDisjointClassesAxiomBinaryConversion parent = getParent();
		ElkDisjointClassesAxiom premise = parent.getOriginalAxiom();
		List<? extends ElkClassExpression> disjoint = premise
				.getClassExpressions();
		return factory.getIndexedSubClassOfAxiomMatch2(
				factory.getIndexedSubClassOfAxiomMatch1(
						parent.getConclusion(factory)),
				factory.getObjectIntersectionOf(
						disjoint.get(parent.getFirstClassPosition()),
						disjoint.get(parent.getSecondClassPosition())),
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

		O visit(ElkDisjointClassesAxiomBinaryConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkDisjointClassesAxiomBinaryConversionMatch1 getElkDisjointClassesAxiomBinaryConversionMatch1(
				ElkDisjointClassesAxiomBinaryConversion parent,
				IndexedSubClassOfAxiomMatch1 conclusionMatch);

	}

}
