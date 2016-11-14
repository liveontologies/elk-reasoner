package org.semanticweb.elk.owl.inferences;

import java.util.AbstractList;
import java.util.List;

import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

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

public abstract class AbstractElkInference implements ElkInference {

	private final static ElkObject.Factory ELK_FACTORY_ = new ElkObjectBaseFactory();

	/**
	 * hash code, computed on demand
	 */
	private int hashCode_ = 0;

	static <T> T failGetPremise(int index) {
		throw new IndexOutOfBoundsException("No premise with index: " + index);
	}

	void checkPremiseIndex(int index) {
		if (index < 0 || index >= getPremiseCount()) {
			failGetPremise(index);
		}
	}

	@Override
	public ElkAxiom getConclusion() {
		return getConclusion(ELK_FACTORY_);
	}

	@Override
	public List<? extends ElkAxiom> getPremises() {
		return new AbstractList<ElkAxiom>() {

			@Override
			public ElkAxiom get(int index) {
				return getPremise(index, ELK_FACTORY_);
			}

			@Override
			public int size() {
				return getPremiseCount();
			}

		};
	}

	@Override
	public int hashCode() {
		if (hashCode_ == 0) {
			hashCode_ = accept(ElkInferenceHash.getHashVisitor());
		}
		// else
		return hashCode_;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		// else
		if (o instanceof ElkInference) {
			return hashCode() == o.hashCode()
					&& ElkInferenceEquality.equals(this, (ElkInference) o);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return ElkInferencePrinter.toString(this);
	}

}
