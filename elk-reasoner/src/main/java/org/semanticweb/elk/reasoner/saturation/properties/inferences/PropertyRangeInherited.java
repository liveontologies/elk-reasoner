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
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.Conclusion.Factory;

/**
 * An {@link ObjectPropertyInference} producing a {@link PropertyRange} from a
 * {@link SubPropertyChain} and {@link IndexedObjectPropertyRangeAxiom}:<br>
 * 
 * <pre>
 *   (1)       (2)
 *  R ⊑ S  [Range(S,D)]
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *      Range(R,D)
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * R = {@link #getSubProperty()}<br>
 * S = {@link #getSuperProperty()}<br>
 * D = {@link #getRange()}<br>
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

	public IndexedObjectProperty getSubProperty() {
		return getProperty();
	}

	public IndexedObjectProperty getSuperProperty() {
		return superProperty_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public SubPropertyChain getFirstPremise(SubPropertyChain.Factory factory) {
		return factory.getSubPropertyChain(getProperty(), getSuperProperty());
	}

	public IndexedObjectPropertyRangeAxiom getSecondPremise(
			IndexedObjectPropertyRangeAxiom.Factory factory) {
		return factory.getIndexedObjectPropertyRangeAxiom(reason_,
				getSuperProperty(), getRange());
	}

	@Override
	public int getPremiseCount() {
		return 2;
	}

	@Override
	public Conclusion getPremise(int index, Factory factory) {
		switch (index) {
		case 0:
			return getFirstPremise(factory);
		case 1:
			return getSecondPremise(factory);
		default:
			return failGetPremise(index);
		}
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
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(PropertyRangeInherited inference);

	}

}
