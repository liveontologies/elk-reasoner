package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SubClassInclusionComposedImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.tracing.Inference;
import org.semanticweb.elk.reasoner.tracing.InferencePrinter;

abstract class AbstractSubClassInclusionComposedInference<S extends IndexedClassExpression>
		extends
			SubClassInclusionComposedImpl<S>
		implements
			SubClassInclusionComposedInference {

	public AbstractSubClassInclusionComposedInference(
			IndexedContextRoot subExpression, S superExpression) {
		super(subExpression, superExpression);
	}

	public S getConclusionSubsumer() {
		return super.getSubsumer();
	}

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 *            
	 * @return the conclusion produced by this inference
	 */
	public SubClassInclusionComposed getConclusion(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getDestination(),
				getSubsumer());
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}
	
	@Override
	public String toString() {
		return InferencePrinter.toString(this);		
	}

	@Override
	public final <O> O accept(Inference.Visitor<O> visitor) {
		return accept((SubClassInclusionComposedInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SaturationInference.Visitor<O> visitor) {
		return accept((SubClassInclusionComposedInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(ClassInference.Visitor<O> visitor) {
		return accept((SubClassInclusionComposedInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SubClassInclusionInference.Visitor<O> visitor) {
		return accept((SubClassInclusionComposedInference.Visitor<O>) visitor);
	}

}
