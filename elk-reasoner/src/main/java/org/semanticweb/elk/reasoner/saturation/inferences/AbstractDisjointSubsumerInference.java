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
package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;

abstract class AbstractDisjointSubsumerInference extends AbstractClassInference
		implements DisjointSubsumerInference {

	private final IndexedClassExpressionList disjointExpressions_;

	private final int position_;

	public AbstractDisjointSubsumerInference(IndexedContextRoot destination,
			IndexedClassExpressionList disjointExpressions, int position) {
		super(destination);
		this.disjointExpressions_ = disjointExpressions;
		this.position_ = position;
	}

	public IndexedClassExpressionList getDisjointExpressions() {
		return disjointExpressions_;
	}

	public int getPosition() {
		return position_;
	}

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	public final DisjointSubsumer getConclusion(
			DisjointSubsumer.Factory factory) {
		return factory.getDisjointSubsumer(getDestination(),
				getDisjointExpressions(), getPosition());
	}

	@Override
	public final <O> O accept(ClassInference.Visitor<O> visitor) {
		return accept((DisjointSubsumerInference.Visitor<O>) visitor);
	}

}
