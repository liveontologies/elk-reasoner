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
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentObjectPropertiesAxiomConversion;

public class ElkEquivalentObjectPropertiesAxiomConversionMatch1 extends
		AbstractInferenceMatch<ElkEquivalentObjectPropertiesAxiomConversion> {

	private final int subPropertyPos_, superPropertyPos_;

	ElkEquivalentObjectPropertiesAxiomConversionMatch1(
			ElkEquivalentObjectPropertiesAxiomConversion parent,
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		super(parent);
		this.subPropertyPos_ = parent.getSubPropertyPosition();
		this.superPropertyPos_ = parent.getSuperPropertyPosition();
	}

	public IndexedSubObjectPropertyOfAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		List<? extends ElkObjectPropertyExpression> members = getParent()
				.getOriginalAxiom().getObjectPropertyExpressions();
		ElkObjectPropertyExpression superPropertyExpression = members
				.get(superPropertyPos_);
		if (superPropertyExpression instanceof ElkObjectProperty) {
			ElkObjectProperty superProperty = (ElkObjectProperty) superPropertyExpression;
			return factory.getIndexedSubObjectPropertyOfAxiomMatch2(
					factory.getIndexedSubObjectPropertyOfAxiomMatch1(
							getParent().getConclusion(factory)),
					members.get(subPropertyPos_), superProperty);
		} else {
			throw new ElkMatchException(
					getParent().getConclusion(factory).getSuperProperty(),
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

		O visit(ElkEquivalentObjectPropertiesAxiomConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkEquivalentObjectPropertiesAxiomConversionMatch1 getElkEquivalentObjectPropertiesAxiomConversionMatch1(
				ElkEquivalentObjectPropertiesAxiomConversion parent,
				IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch);

	}

}
