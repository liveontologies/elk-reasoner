/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.properties.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * A {@link PropertyRange} for the sub-property of a {@link SubPropertyChain}
 * obtained from an {@link IndexedObjectPropertyRangeAxiom} for the
 * super-property of this {@link SubPropertyChain}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropertyRangeInherited extends AbstractPropertyRangeInference {

	/**
	 * The super-property for which the range is stated
	 */
	private final IndexedObjectProperty superProperty_;

	/**
	 * The {@link ElkAxiom} that states the range of {@link #superProperty_}
	 */
	private final ElkAxiom reason_;

	public PropertyRangeInherited(IndexedObjectProperty subProperty,
			IndexedObjectProperty superProperty, IndexedClassExpression range,
			ElkAxiom reason) {
		super(subProperty, range);
		this.superProperty_ = superProperty;
		this.reason_ = reason;
	}

	public IndexedObjectProperty getSuperProperty() {
		return superProperty_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public SubPropertyChain getPremise(SubPropertyChain.Factory factory) {
		return factory.getSubPropertyChain(getProperty(), getSuperProperty());
	}

	public PropertyRange getConclusion(PropertyRange.Factory factory) {
		return factory.getPropertyRange(getProperty(), getRange());
	}

	public IndexedObjectPropertyRangeAxiom getSideCondition(
			IndexedObjectPropertyRangeAxiom.Factory factory) {
		return factory.getIndexedObjectPropertyRangeAxiom(reason_,
				getSuperProperty(), getRange());
	}

	@Override
	public final <O> O accept(PropertyRangeInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {

		public O visit(PropertyRangeInherited inference);

	}

}
