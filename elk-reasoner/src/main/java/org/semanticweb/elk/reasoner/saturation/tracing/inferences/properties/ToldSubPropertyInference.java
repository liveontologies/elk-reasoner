/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Property hierarchy inference based on a told sub-property subsumption. RR <=
 * SS if H <= SS and RR is a told sub-chain of H. This class stores H as the
 * premise. RR <= H is a side condition.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class ToldSubPropertyInference extends
		SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain> implements
		ObjectPropertyInference {

	/**
	 * The inferred sub-property of the super-chain for which the inference is
	 * performed by unfolding under told sub-chain of this property
	 */
	private final IndexedObjectProperty premise_;

	/**
	 * The {@link ElkAxiom} responsible for the told sub-chain of the premise
	 */
	private final ElkAxiom reason_;

	public ToldSubPropertyInference(IndexedPropertyChain subChain,
			IndexedObjectProperty premise, IndexedPropertyChain superChain,
			ElkAxiom reason) {
		super(subChain, superChain);
		this.premise_ = premise;
		this.reason_ = reason;
	}

	public SubPropertyChain<IndexedObjectProperty, IndexedPropertyChain> getPremise() {
		return new SubPropertyChain<IndexedObjectProperty, IndexedPropertyChain>(
				premise_, getSuperPropertyChain());
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return "Told sub-chain: " + getSubPropertyChain() + " => "
				+ getSuperPropertyChain() + ", premise: " + premise_ + " => "
				+ getSuperPropertyChain();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ToldSubPropertyInference)) {
			return false;
		}

		ToldSubPropertyInference inf = (ToldSubPropertyInference) obj;

		return premise_.equals(inf.premise_)
				&& getSubPropertyChain().equals(inf.getSubPropertyChain())
				&& getSuperPropertyChain().equals(inf.getSuperPropertyChain());
	}

	@Override
	public int hashCode() {
		return HashGenerator.combineListHash(premise_.hashCode(),
				getSubPropertyChain().hashCode(), getSuperPropertyChain()
						.hashCode());
	}
}
