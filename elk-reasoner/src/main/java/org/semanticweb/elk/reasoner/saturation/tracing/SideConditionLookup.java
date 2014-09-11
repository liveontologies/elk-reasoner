/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRuleWithAxiomBinding;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionLookup {

	private ClassInferenceVisitor<Void, ElkAxiom> classAxiomGetter = new AbstractClassInferenceVisitor<Void, ElkAxiom>() {

		@Override
		protected ElkAxiom defaultTracedVisit(ClassInference conclusion,
				Void ignored) {
			// by default rules aren't bound to axioms, only some are
			return null;
		}

		@Override
		public ElkAxiom visit(SubClassOfSubsumer<?> inference,
				Void ignored) {
			// looking for a super class rule
			SuperClassFromSubClassRuleWithAxiomBinding ruleWithBinding = find(
					inference.getPremise().getExpression().getCompositionRuleHead(),
					new SimpleTypeBasedMatcher<LinkedSubsumerRule, SuperClassFromSubClassRuleWithAxiomBinding>(
							SuperClassFromSubClassRuleWithAxiomBinding.class));
			// if we found a rule with axiom binding, we can then look for the
			// asserted axiom which corresponds to this derived subsumer
			if (ruleWithBinding != null) {
				return ruleWithBinding.getAxiomForConclusion(inference.getExpression());
			}

			return null;
		}

	};

	public ElkAxiom lookup(ClassInference inference) {
		return inference.acceptTraced(classAxiomGetter, null);
	}

	/*public ElkObjectPropertyAxiom lookup(ObjectPropertyInference inference) {
		// TODO
		return null;
	}*/

	// FIXME Why can't we have this for any Link<LinkRule>?
	private static <O> O find(LinkedSubsumerRule link,
			Matcher<LinkedSubsumerRule, O> matcher) {
		LinkedSubsumerRule candidate = link;
		for (;;) {
			if (candidate == null)
				return null;
			O match = matcher.match(candidate);
			if (match != null)
				return match;
			candidate = candidate.next();
		}
	}
}
