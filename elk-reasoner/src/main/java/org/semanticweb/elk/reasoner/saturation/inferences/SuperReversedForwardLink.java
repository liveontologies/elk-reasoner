package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.factories.IndexedSubObjectPropertyOfAxiomFactory;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.BackwardLinkInferenceVisitor;

/**
 * A {@link BackwardLink} that is obtained by reversing a given
 * {@link ForwardLink} and taking its super-property.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SuperReversedForwardLink extends AbstractBackwardLinkInference {

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

	public SuperReversedForwardLink(ForwardLink premise,
			IndexedObjectProperty superProperty, ElkAxiom reason) {
		super(premise.getTarget(), superProperty, premise.getConclusionRoot());
		this.subChain_ = premise.getForwardChain();
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getOriginRoot();
	}

	public IndexedPropertyChain getSubChain() {
		return this.subChain_;
	}
	
	public ElkAxiom getReason() {
		return this.reason_;
	}

	public ForwardLink getFirstPremise(ForwardLink.Factory factory) {
		return factory.getForwardLink(getInferenceRoot(), subChain_,
				getConclusionRoot());
	}
	
	public IndexedSubObjectPropertyOfAxiom getSideCondition(
			IndexedSubObjectPropertyOfAxiomFactory factory) {
		return factory.getIndexedSubObjectPropertyOfAxiom(reason_, subChain_,
				getBackwardRelation());
	}

	public SubPropertyChain getSecondPremise(SubPropertyChain.Factory factory) {
		return factory.getSubPropertyChain(subChain_, getBackwardRelation());
	}

	@Override
	public <I, O> O accept(BackwardLinkInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
