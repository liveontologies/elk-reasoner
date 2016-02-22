package org.semanticweb.elk.reasoner.saturation.properties.inferences;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.PropertyRangeImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;
import org.semanticweb.elk.reasoner.tracing.Inference;

public abstract class AbstractPropertyRangeInference extends PropertyRangeImpl
		implements PropertyRangeInference {

	protected AbstractPropertyRangeInference(IndexedObjectProperty property,
			IndexedClassExpression range) {
		super(property, range);
	}

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	public PropertyRange getConclusion(PropertyRange.Factory factory) {
		return factory.getPropertyRange(getProperty(), getRange());
	}

	// we assume that different objects represent different inferences
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public final <O> O accept(Inference.Visitor<O> visitor) {
		return accept((PropertyRangeInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SaturationInference.Visitor<O> visitor) {
		return accept((PropertyRangeInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(ObjectPropertyInference.Visitor<O> visitor) {
		return accept((PropertyRangeInference.Visitor<O>) visitor);
	}

}
