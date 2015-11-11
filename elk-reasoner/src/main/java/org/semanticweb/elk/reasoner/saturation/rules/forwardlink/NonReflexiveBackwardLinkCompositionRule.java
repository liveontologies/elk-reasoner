package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

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

import java.util.Collection;
import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A {@link ForwardLinkRule} applied when processing a {@link ForwardLink}
 * producing {@link BackwardLink}s resulted by composition of this
 * {@link ForwardLink} with existing non-reflexive {@link BackwardLink}s using
 * property chain axioms
 * 
 * @see ReflexiveBackwardLinkCompositionRule
 * 
 * @author "Yevgeny Kazakov"
 */
public class NonReflexiveBackwardLinkCompositionRule extends
		AbstractForwardLinkRule {

	/**
	 * 
	 */
	private final ForwardLink forwardLink_;

	/**
	 * @param forwardLink
	 */
	private NonReflexiveBackwardLinkCompositionRule(ForwardLink forwardLink) {
		this.forwardLink_ = forwardLink;
	}

	public static final String NAME = "ForwardLink Non-Reflexive BackwardLink Composition";

	/**
	 * @param link
	 *            a {@link ForwardLink} for which to create the rule
	 * @return {@link NonReflexiveBackwardLinkCompositionRule}s for the given
	 *         {@link ForwardLink}
	 */
	public static NonReflexiveBackwardLinkCompositionRule getRuleFor(
			ForwardLink link) {
		return new NonReflexiveBackwardLinkCompositionRule(link);
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(ForwardLink premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		/* compose the link with all non-reflexive backward links */
		SaturatedPropertyChain linkSaturation = this.forwardLink_
				.getForwardChain().getSaturated();
		final Multimap<IndexedObjectProperty, IndexedComplexPropertyChain> comps = linkSaturation
				.getCompositionsByLeftSubProperty();
		final Map<IndexedObjectProperty, ? extends SubContextPremises> subContextMap = premises
				.getSubContextPremisesByObjectProperty();

		for (IndexedObjectProperty backwardRelation : new LazySetIntersection<IndexedObjectProperty>(
				comps.keySet(), subContextMap.keySet())) {

			Collection<IndexedComplexPropertyChain> compositions = comps
					.get(backwardRelation);
			SubContextPremises subPremises = subContextMap
					.get(backwardRelation);

			for (IndexedComplexPropertyChain composition : compositions)
				for (IndexedContextRoot source : subPremises.getLinkedRoots()) {
					IndexedObjectSomeValuesFrom.Helper.produceComposedLink(producer, source,
							backwardRelation, premises.getRoot(),
							forwardLink_.getForwardChain(),
							forwardLink_.getTarget(), composition);
				}
		}
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public void accept(ForwardLinkRuleVisitor<?> visitor, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

}
