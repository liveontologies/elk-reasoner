/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@code Conclusion} representing that some member of an
 * {@link IndexedDisjointnessAxiom} was derived as a subsumer in the
 * {@link Context}. In other words, the (disjoint) union of the
 * {@link IndexedDisjointnessAxiom} members is derived as a subsumer.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class DisjointSubsumer extends AbstractConclusion {

	private static ContradicitonCompositionRule THIS_COMPOSITION_RULE_ = new ContradicitonCompositionRule();

	private final IndexedDisjointnessAxiom axiom_;

	public DisjointSubsumer(IndexedDisjointnessAxiom axiom) {
		axiom_ = axiom;
	}

	public IndexedDisjointnessAxiom getAxiom() {
		return axiom_;
	}

	@Override
	public void accept(CompositionRuleVisitor ruleAppVisitor,
			SaturationStateWriter writer, Context context) {
		if (context.inconsistForDisjointnessAxiom(axiom_)) {
			ruleAppVisitor.visit(THIS_COMPOSITION_RULE_, this, context, writer);
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	@Override
	public String toString() {
		return axiom_.toString();
	}

	/**
	 * The composition rule applied when processing a {@link DisjointSubsumer}
	 * producing {@link Contradiction} caused by violation of disjointness
	 * constrains of this {@link DisjointSubsumer}
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	public static class ContradicitonCompositionRule implements
			Rule<DisjointSubsumer> {

		private static final String NAME_ = "Contradiction by Disjointness Axiom";

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(DisjointSubsumer premise, Context context,
				SaturationStateWriter writer) {
			writer.produce(context, Contradiction.getInstance());
		}

	}

}