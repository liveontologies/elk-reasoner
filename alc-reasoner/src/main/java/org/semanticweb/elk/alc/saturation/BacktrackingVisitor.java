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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleDecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.PossibleConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;

public class BacktrackingVisitor extends RevertingVisitor {

	private final ConclusionProducer producer_;

	public BacktrackingVisitor(ConclusionProducer conclusionProducer) {
		super(conclusionProducer);
		this.producer_ = conclusionProducer;
	}

	@Override
	public void visitSubsumer(Subsumer conclusion, Context input) {
		IndexedClassExpression expression = conclusion.getExpression();
		if (conclusion instanceof PossibleConclusion) {
			producer_.produce(input.getRoot(), new NegatedSubsumerImpl(
					expression));
			return;
		}
		if (input.getMaskedPossibleComposedSubsumers().contains(expression)) {
			producer_.produce(input.getRoot(),
					new PossibleComposedSubsumerImpl(expression));
		}
		if (input.getDisjunctions().keySet().contains(expression))
			producer_.produce(input.getRoot(),
					new PossibleDecomposedSubsumerImpl(expression));
	}

}
