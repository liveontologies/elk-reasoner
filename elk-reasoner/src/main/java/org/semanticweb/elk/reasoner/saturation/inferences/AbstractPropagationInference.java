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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;

abstract class AbstractPropagationInference extends AbstractSubClassInference
		implements PropagationInference {

	private final IndexedObjectSomeValuesFrom carry_;

	public AbstractPropagationInference(IndexedContextRoot root,
			IndexedObjectProperty relation, IndexedObjectSomeValuesFrom carry) {
		super(root, relation);
		this.carry_ = carry;
	}

	public IndexedObjectProperty getRelation() {
		return getSubDestination();
	}

	public IndexedObjectSomeValuesFrom getCarry() {
		return this.carry_;
	}

	public IndexedObjectSomeValuesFrom getConclusionCarry() {
		return getCarry();
	}

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	public final Propagation getConclusion(Propagation.Factory factory) {
		return factory.getPropagation(getDestination(), getRelation(),
				getCarry());
	}

	@Override
	public final <O> O accept(SubClassInference.Visitor<O> visitor) {
		return accept((PropagationInference.Visitor<O>) visitor);
	}

}
