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
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomSubClassConversion;

public class ElkEquivalentClassesAxiomSubClassConversionMatch1 extends
		AbstractInferenceMatch<ElkEquivalentClassesAxiomSubClassConversion> {

	ElkEquivalentClassesAxiomSubClassConversionMatch1(
			ElkEquivalentClassesAxiomSubClassConversion parent,
			IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		super(parent);
	}

	public IndexedSubClassOfAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		ElkEquivalentClassesAxiomSubClassConversion parent = getParent();
		ElkEquivalentClassesAxiom premise = parent.getOriginalAxiom();
		List<? extends ElkClassExpression> members = premise
				.getClassExpressions();
		return factory.getIndexedSubClassOfAxiomMatch2(
				factory.getIndexedSubClassOfAxiomMatch1(
						parent.getConclusion(factory)),
				members.get(parent.getSubClassPosition()),
				members.get(parent.getSuperClassPosition()));
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

		O visit(ElkEquivalentClassesAxiomSubClassConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkEquivalentClassesAxiomSubClassConversionMatch1 getElkEquivalentClassesAxiomSubClassConversionMatch1(
				ElkEquivalentClassesAxiomSubClassConversion parent,
				IndexedSubClassOfAxiomMatch1 conclusionMatch);

	}

}
