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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *           (1)               (2)
 *  ObjectPropertyRange(R C)  S ⊑ R
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *    ObjectPropertyRange(S C)
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkPropertyRangePropertyUnfolding extends AbstractElkInference {

	private final ElkObjectPropertyExpression subProperty_, superProperty_;

	private final ElkClassExpression range_;

	ElkPropertyRangePropertyUnfolding(ElkObjectPropertyExpression superProperty,
			ElkClassExpression range, ElkObjectPropertyExpression subProperty) {
		this.superProperty_ = superProperty;
		this.range_ = range;
		this.subProperty_ = subProperty;
	}

	public ElkObjectPropertyExpression getSubProperty() {
		return subProperty_;
	}

	public ElkObjectPropertyExpression getSuperProperty() {
		return superProperty_;
	}

	public ElkClassExpression getRange() {
		return range_;
	}

	public ElkObjectPropertyRangeAxiom getFirstPremise(
			ElkObjectFactory factory) {
		return factory.getObjectPropertyRangeAxiom(superProperty_, range_);
	}

	public ElkSubObjectPropertyOfAxiom getSecondPremise(
			ElkObjectFactory factory) {
		return factory.getSubObjectPropertyOfAxiom(subProperty_,
				superProperty_);
	}

	public ElkObjectPropertyRangeAxiom getConclusion(ElkObjectFactory factory) {
		return factory.getObjectPropertyRangeAxiom(subProperty_, range_);
	}

	public <O> O accept(Visitor<O> visitor) {
		return visitor.visit(this);
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

		ElkPropertyRangePropertyUnfolding getElkPropertyRangePropertyUnfolding(
				ElkObjectPropertyExpression superProperty,
				ElkClassExpression range,
				ElkObjectPropertyExpression subProperty);

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

		O visit(ElkPropertyRangePropertyUnfolding inference);

	}

}
