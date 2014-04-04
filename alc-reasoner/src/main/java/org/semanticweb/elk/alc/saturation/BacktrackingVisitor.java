package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
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

import org.semanticweb.elk.alc.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ConjectureNonSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.LocalDeterministicConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.LocalPossibleConclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleDecomposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.LocalConclusionVisitor;

/**
 * A {@link LocalConclusionVisitor} that reverts the visited
 * {@link LocalDeterministicConclusion}s and produces the complements of the
 * visited {@link LocalPossibleConclusion}s in the {@link Context} given as the
 * second argument. Returns {@code true} only for
 * {@link LocalPossibleConclusion}s. This is useful to know when backtracking
 * should stop.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class BacktrackingVisitor extends RevertingVisitor {

	private final ConclusionProducer producer_;

	public BacktrackingVisitor(ConclusionProducer conclusionProducer) {
		super(conclusionProducer);
		this.producer_ = conclusionProducer;
	}

	@Override
	public Boolean visit(PossibleComposedSubsumer conclusion, Context input) {
		producer_.produce(new NegatedSubsumerImpl(conclusion.getExpression()));
		return false;
	}

	@Override
	public Boolean visit(PossibleDecomposedSubsumer conclusion, Context input) {
		producer_.produce(new NegatedSubsumerImpl(conclusion.getExpression()));
		return false;
	}

	@Override
	public Boolean visit(ConjectureNonSubsumer conclusion, Context input) {
		producer_
				.produce(new DecomposedSubsumerImpl(conclusion.getExpression()));
		return true;
	}

}
