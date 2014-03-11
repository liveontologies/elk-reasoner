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

import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.PossibleConclusionVisitor;

public class BacktrackingVisitor implements
		PossibleConclusionVisitor<Context, Void> {

	private final ConclusionProducer producer_;

	public BacktrackingVisitor(ConclusionProducer conclusionProducer) {
		this.producer_ = conclusionProducer;
	}

	public void visitSubsumer(Subsumer conclusion, Context input) {
		producer_.produce(input.getRoot(),
				new NegatedSubsumerImpl(conclusion.getExpression()));
	}

	@Override
	public Void visit(ComposedSubsumer conclusion, Context input) {
		visitSubsumer(conclusion, input);
		return null;
	}

	@Override
	public Void visit(DecomposedSubsumer conclusion, Context input) {
		visitSubsumer(conclusion, input);
		return null;
	}

}
