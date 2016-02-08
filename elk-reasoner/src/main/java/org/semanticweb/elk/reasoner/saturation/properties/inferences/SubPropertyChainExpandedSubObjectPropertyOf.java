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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

/**
 * A {@link SubPropertyChain} obtained from a {@link SubPropertyChain} by
 * unfolding a super-property under an {@link IndexedSubObjectPropertyOfAxiom}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class SubPropertyChainExpandedSubObjectPropertyOf
		extends
			AbstractSubPropertyChainInference {

	/**
	 * The inferred sub-property of the super-chain for which the inference is
	 * performed by unfolding under told sub-chain of this property
	 */
	private final IndexedObjectProperty middleChain_;

	/**
	 * The {@link ElkAxiom} responsible for the told sub-chain of the premise
	 */
	private final ElkAxiom reason_;

	public SubPropertyChainExpandedSubObjectPropertyOf(
			IndexedPropertyChain firstChain,
			IndexedObjectProperty secondProperty,
			IndexedObjectProperty thirdProperty, ElkAxiom reason) {
		super(firstChain, thirdProperty);
		this.middleChain_ = secondProperty;
		this.reason_ = reason;
	}

	public IndexedObjectProperty getPremiseSuperChain() {
		return middleChain_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public SubPropertyChain getFirstPremise() {
		return FACTORY.getSubPropertyChain(middleChain_, getSuperChain());
	}

	public IndexedSubObjectPropertyOfAxiom getSecondPremise() {
		return FACTORY.getIndexedSubObjectPropertyOfAxiom(reason_,
				getSubChain(), middleChain_);
	}

	@Override
	public final <O> O accept(SubPropertyChainInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {

		public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference);

	}

}
