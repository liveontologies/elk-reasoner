/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.properties;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
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
public class SubPropertyChainExpanded extends AbstractSubPropertyChainInference {

	/**
	 * The inferred sub-property of the super-chain for which the inference is
	 * performed by unfolding under told sub-chain of this property
	 */
	private final IndexedPropertyChain middleChain_;

	/**
	 * The {@link ElkAxiom} responsible for the told sub-chain of the premise
	 */
	private final ElkAxiom reason_;

	public SubPropertyChainExpanded(IndexedPropertyChain firstChain,
			IndexedPropertyChain secondChain, IndexedPropertyChain thirdChain,
			ElkAxiom reason) {
		super(firstChain, thirdChain);
		this.middleChain_ = secondChain;
		this.reason_ = reason;
	}

	public SubPropertyChain getPremise() {
		return new SubPropertyChainImpl(middleChain_, getSuperChain());
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	@Override
	public String toString() {
		return "Expanded sub-chain: " + getSubChain() + " => "
				+ getSuperChain() + ", premise: " + middleChain_ + " => "
				+ getSuperChain() + ", reason: " + reason_;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SubPropertyChainExpanded)) {
			return false;
		}

		SubPropertyChainExpanded inf = (SubPropertyChainExpanded) obj;

		return middleChain_.equals(inf.middleChain_)
				&& getSubChain().equals(inf.getSubChain())
				&& getSuperChain().equals(inf.getSuperChain());
	}

	@Override
	public int hashCode() {
		return HashGenerator.combineListHash(middleChain_.hashCode(),
				getSubChain().hashCode(), getSuperChain().hashCode());
	}

	@Override
	public <I, O> O accept(SubPropertyChainInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
}
