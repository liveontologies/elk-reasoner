package org.semanticweb.elk.owl.inferences;

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

import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *  C ⊑ ObjectUnionOf(D)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *        C ⊑ D
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionSingletonObjectUnionOfDecomposition
		extends AbstractElkInference {

	private final static String NAME_ = "Singleton Disjunction Decomposition";

	private final ElkClassExpression subExpression_;

	private final ElkClassExpression disjunct_;

	ElkClassInclusionSingletonObjectUnionOfDecomposition(
			ElkClassExpression subExpression, ElkClassExpression disjunct) {
		this.subExpression_ = subExpression;
		this.disjunct_ = disjunct;
	}

	public ElkClassExpression getSubExpression() {
		return subExpression_;
	}

	public ElkClassExpression getDisjunct() {
		return disjunct_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkSubClassOfAxiom getPremise(int index, ElkObject.Factory factory) {
		checkPremiseIndex(index);
		return factory.getSubClassOfAxiom(subExpression_,
				factory.getObjectUnionOf(Collections.singletonList(disjunct_)));
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(subExpression_, disjunct_);
	}

	@Override
	public <O> O accept(ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkClassInclusionSingletonObjectUnionOfDecomposition getElkClassInclusionSingletonObjectUnionOfDecomposition(
				ElkClassExpression subExpression, ElkClassExpression disjunct);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkClassInclusionSingletonObjectUnionOfDecomposition inference);

	}

}
