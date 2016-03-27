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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;

public class PropertyRangeInheritedMatch1
		extends AbstractInferenceMatch<PropertyRangeInherited>
		implements IndexedObjectPropertyRangeAxiomMatch1Watch {

	private final ElkObjectProperty subPropertyMatch_;

	PropertyRangeInheritedMatch1(PropertyRangeInherited parent,
			PropertyRangeMatch1 conclusionMatch) {
		super(parent);
		subPropertyMatch_ = conclusionMatch.getPropertyMatch();
	}

	public ElkObjectProperty getSubPropertyMatch() {
		return subPropertyMatch_;
	}

	public PropertyRangeMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getPropertyRangeMatch1(
				getParent().getConclusion(factory), subPropertyMatch_);
	}

	public IndexedObjectPropertyRangeAxiomMatch1 getSecondPremiseMatch(
			ConclusionMatchExpressionFactory factory) {
		return factory.getIndexedObjectPropertyRangeAxiomMatch1(
				getParent().getSecondPremise(factory));
	}

	@Override
	public <O> O accept(InferenceMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(
			IndexedObjectPropertyRangeAxiomMatch1Watch.Visitor<O> visitor) {
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

		O visit(PropertyRangeInheritedMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		PropertyRangeInheritedMatch1 getPropertyRangeInheritedMatch1(
				PropertyRangeInherited parent,
				PropertyRangeMatch1 conclusionMatch);

	}

}
