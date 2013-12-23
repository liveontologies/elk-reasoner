package org.semanticweb.elk.reasoner.saturation.conclusions;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;

/**
 * A {@link ConclusionVisitor} that applies decomposition rules for visited
 * {@link Conclusion}s using the provided {@link CompositionRuleVisitor} to
 * track rule applications and {@link SaturationStateWriter} to output the
 * {@link Conclusion}s of the applied rules.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionCompositionRuleApplicationVisitor implements
		ConclusionVisitor<Boolean> {

	/**
	 * {@link CompositionRuleVisitor} to track rule applications
	 */
	private final CompositionRuleVisitor ruleAppVisitor_;

	/**
	 * {@link SaturationStateWriter} to output the {@link Conclusion}s of the
	 * applied rules
	 */
	private final SaturationStateWriter writer_;

	public ConclusionCompositionRuleApplicationVisitor(
			CompositionRuleVisitor ruleAppVisitor, SaturationStateWriter writer) {
		this.writer_ = writer;
		this.ruleAppVisitor_ = ruleAppVisitor;
	}

	public Boolean defaultVisit(Conclusion conclusion, Context context) {
		conclusion.accept(ruleAppVisitor_, writer_, context);
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer negSCE, Context context) {
		return defaultVisit(negSCE, context);
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Context context) {
		return defaultVisit(posSCE, context);
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return defaultVisit(link, context);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return defaultVisit(link, context);
	}

	@Override
	public Boolean visit(Contradiction bot, Context context) {
		return defaultVisit(bot, context);
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return defaultVisit(propagation, context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return defaultVisit(disjointnessAxiom, context);
	}

}
