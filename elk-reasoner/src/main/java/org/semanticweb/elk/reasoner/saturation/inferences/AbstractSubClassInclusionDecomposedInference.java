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
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SubClassInclusionDecomposedImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.semanticweb.elk.reasoner.tracing.TracingInferencePrinter;

abstract class AbstractSubClassInclusionDecomposedInference
		extends SubClassInclusionDecomposedImpl
		implements SubClassInclusionDecomposedInference {

	public AbstractSubClassInclusionDecomposedInference(
			IndexedContextRoot subExpression,
			IndexedClassExpression superExpression) {
		super(subExpression, superExpression);
	}

	public final IndexedClassExpression getConclusionSubsumer() {
		return getSubsumer();
	}

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	public final SubClassInclusionDecomposed getConclusion(
			SubClassInclusionDecomposed.Factory factory) {
		return factory.getSubClassInclusionDecomposed(getDestination(),
				getSubsumer());
	}

	@Override
	public final int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public final boolean equals(Object o) {
		return this == o;
	}
	
	@Override
	public final String toString() {
		return TracingInferencePrinter.toString(this);		
	}

	@Override
	public final <O> O accept(TracingInference.Visitor<O> visitor) {
		return accept(
				(SubClassInclusionDecomposedInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SaturationInference.Visitor<O> visitor) {
		return accept(
				(SubClassInclusionDecomposedInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(ClassInference.Visitor<O> visitor) {
		return accept(
				(SubClassInclusionDecomposedInference.Visitor<O>) visitor);
	}

	@Override
	public final <O> O accept(SubClassInclusionInference.Visitor<O> visitor) {
		return accept(
				(SubClassInclusionDecomposedInference.Visitor<O>) visitor);
	}

}
