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

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.SubsumerDecompositionVisitor;

/**
 * 
 * A {@link ConclusionVisitor} that applies minimal number of rules for visited
 * {@link Conclusion}s. All composition rules for all {@link Conclusion}s are
 * applied using the provided {@link CompositionRuleVisitor}. In addition,
 * decomposition rules are applied but only for {@link DecomposedSubsumer}s
 * using the provided {@link SubsumerDecompositionVisitor}. This is sufficient
 * to ensure completeness when the results of rules are saved in contexts, but
 * also means that the results of rule application are non-deterministic since
 * {@link DecomposedSubsumer}s and {@link ComposedSubsumer}s are identified when
 * saved in {@link Context}s. The output of the applied rules are saved using
 * the provided {@link SaturationStateWriter}.
 * 
 * @see ConclusionRuleApplicationVisitorMax
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class ConclusionRuleApplicationVisitorMin extends
		ConclusionCompositionRuleApplicationVisitor implements
		ConclusionVisitor<Boolean> {

	/**
	 * {@ SubsumerDecompositionVisitor} to apply decomposition rules
	 */
	private final SubsumerDecompositionVisitor decompRuleAppVisitor_;

	public ConclusionRuleApplicationVisitorMin(
			CompositionRuleVisitor ruleAppVisitor,
			SubsumerDecompositionVisitor decompVisitor,
			SaturationStateWriter writer) {
		super(ruleAppVisitor, writer);
		this.decompRuleAppVisitor_ = decompVisitor;
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Context context) {
		super.visit(posSCE, context);
		posSCE.getExpression().accept(decompRuleAppVisitor_, context);
		return true;
	}

}
