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
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch2;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomNaryConversion;

public class ElkDisjointClassesAxiomNaryConversionMatch1
		extends AbstractInferenceMatch<ElkDisjointClassesAxiomNaryConversion> {

	ElkDisjointClassesAxiomNaryConversionMatch1(
			ElkDisjointClassesAxiomNaryConversion parent,
			IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		super(parent);
	}

	public IndexedDisjointClassesAxiomMatch2 getConclusionMatch(
			ConclusionMatchExpressionFactory factory) {
		ElkDisjointClassesAxiomNaryConversion parent = getParent();
		ElkDisjointClassesAxiom premise = parent.getOriginalAxiom();
		List<? extends ElkClassExpression> disjoint = premise
				.getClassExpressions();
		return factory.getIndexedDisjointClassesAxiomMatch2(
				factory.getIndexedDisjointClassesAxiomMatch1(
						parent.getConclusion(factory)),
				disjoint);
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

		O visit(ElkDisjointClassesAxiomNaryConversionMatch1 inferenceMatch1);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkDisjointClassesAxiomNaryConversionMatch1 getElkDisjointClassesAxiomNaryConversionMatch1(
				ElkDisjointClassesAxiomNaryConversion parent,
				IndexedDisjointClassesAxiomMatch1 conclusionMatch);

	}

}
