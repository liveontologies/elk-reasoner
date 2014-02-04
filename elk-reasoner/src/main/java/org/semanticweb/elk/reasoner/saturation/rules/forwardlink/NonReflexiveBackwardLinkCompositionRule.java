package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * A {@link ForwardLinkRule} applied when processing this {@link ForwardLink}
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

	private static final String NAME_ = "ForwardLink Non-Reflexive BackwardLink Composition";

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
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(ForwardLink premise, Context context,
			ConclusionProducer producer) {
		/* compose the link with all non-reflexive backward links */
		final Multimap<IndexedPropertyChain, IndexedPropertyChain> comps = this.forwardLink_
				.getRelation().getSaturated()
				.getCompositionsByLeftSubProperty();
		final Multimap<IndexedPropertyChain, Context> backLinks = context
				.getBackwardLinksByObjectProperty();

		for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
				comps.keySet(), backLinks.keySet())) {

			Collection<IndexedPropertyChain> compositions = comps
					.get(backwardRelation);
			Collection<Context> sources = backLinks.get(backwardRelation);

			for (IndexedPropertyChain composition : compositions)
				for (Context source : sources) {
					producer.produce(this.forwardLink_.getTarget(),
							new BackwardLink(source, composition));
				}
		}
	}

	@Override
	public void accept(ForwardLinkRuleVisitor visitor, ForwardLink premise,
			Context context, ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}