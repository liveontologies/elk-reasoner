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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;

/**
 * A {@link ClassInference} producing a {@link BackwardLink} from a
 * {@link ForwardLink} and an {@link IndexedSubObjectPropertyOfAxiom}:<br>
 * 
 * <pre>
 *      (1)         (2)
 *  [C] ⊑ <∃P>.D  [P ⊑ R]
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *       C ⊑ <∃R>.[D]
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getConclusionSource()} <br>
 * P = {@link #getSubChain()}<br>
 * R = {@link #getRelation()}<br>
 * D = {@link #getDestination()}<br>
 * 
 * @see ForwardLink#getRelation()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class BackwardLinkReversedExpanded
		extends AbstractBackwardLinkInference {

	/**
	 * The sub chain of the property of this link that occurs in the premise
	 * forward link
	 */
	private final IndexedPropertyChain subChain_;

	/**
	 * The {@link ElkAxiom} that yields the inclusion between {@link #subChain_}
	 * and backward relation
	 */
	private final ElkAxiom reason_;

	public BackwardLinkReversedExpanded(ForwardLink premise,
			IndexedObjectProperty superProperty, ElkAxiom reason) {
		super(premise.getTarget(), superProperty, premise.getDestination());
		this.subChain_ = premise.getRelation();
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getTraceRoot();
	}

	public IndexedContextRoot getConclusionSource() {
		return getSource();
	}

	public IndexedPropertyChain getSubChain() {
		return this.subChain_;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	public ForwardLink getFirstPremise(ForwardLink.Factory factory) {
		return factory.getForwardLink(getOrigin(), subChain_, getDestination());
	}

	public IndexedSubObjectPropertyOfAxiom getSecondPremise(
			IndexedSubObjectPropertyOfAxiom.Factory factory) {
		return factory.getIndexedSubObjectPropertyOfAxiom(reason_, subChain_,
				getRelation());
	}

	@Override
	public final <O> O accept(BackwardLinkInference.Visitor<O> visitor) {
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

		public O visit(BackwardLinkReversedExpanded inference);

	}

}
