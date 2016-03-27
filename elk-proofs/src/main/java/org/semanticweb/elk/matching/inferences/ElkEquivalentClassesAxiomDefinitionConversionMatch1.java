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

import org.semanticweb.elk.matching.ElkMatchException;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedDefinitionAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDefinitionAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomDefinitionConversion;

public class ElkEquivalentClassesAxiomDefinitionConversionMatch1 extends
		AbstractInferenceMatch<ElkEquivalentClassesAxiomDefinitionConversion> {

	private final int definedClassPos_, definitionPos_;

	ElkEquivalentClassesAxiomDefinitionConversionMatch1(
			ElkEquivalentClassesAxiomDefinitionConversion parent,
			IndexedDefinitionAxiomMatch1 conclusionMatch) {
		super(parent);
		this.definedClassPos_ = parent.getDefinedClassPosition();
		this.definitionPos_ = parent.getDefinitionPosition();
	}

	public IndexedDefinitionAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		List<? extends ElkClassExpression> members = getParent()
				.getOriginalAxiom().getClassExpressions();
		ElkClassExpression definedClassExpression = members
				.get(definedClassPos_);
		if (definedClassExpression instanceof ElkClass) {
			return factory.getIndexedDefinitionAxiomMatch2(
					factory.getIndexedDefinitionAxiomMatch1(
							getParent().getConclusion(factory)),
					(ElkClass) definedClassExpression,
					members.get(definitionPos_));
		}
		// else
		throw new ElkMatchException(
				getParent().getConclusion(factory).getDefinedClass(),
				definedClassExpression);
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

		O visit(ElkEquivalentClassesAxiomDefinitionConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkEquivalentClassesAxiomDefinitionConversionMatch1 getElkEquivalentClassesAxiomDefinitionConversionMatch1(
				ElkEquivalentClassesAxiomDefinitionConversion parent,
				IndexedDefinitionAxiomMatch1 conclusionMatch);

	}

}
