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
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BacktrackedBackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleComposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PossibleDecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.NegatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;

public class BacktrackingVisitor extends
		AbstractConclusionVisitor<Context, Void> {

	private final ConclusionProducer producer_;

	public BacktrackingVisitor(ConclusionProducer conclusionProducer) {
		this.producer_ = conclusionProducer;
	}

	@Override
	protected Void defaultVisit(Conclusion conclusion, Context input) {
		// does nothing by default
		return null;
	}

	public void visitSubsumer(Subsumer conclusion, Context input) {
		IndexedClassExpression expression = conclusion.getExpression();
		if (input.getDisjunctions().keySet().contains(expression)
				|| input.getPropagatedSubsumers().contains(expression))
			producer_.produce(input.getRoot(), new NegatedSubsumerImpl(
					expression));
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

	@Override
	public Void visit(NegatedSubsumer conclusion, Context input) {
		IndexedClassExpression negatedExpression = conclusion
				.getNegatedExpression();
		if (input.getDisjunctions().keySet().contains(negatedExpression))
			producer_.produce(input.getRoot(),
					new PossibleDecomposedSubsumerImpl(negatedExpression));
		if (input.getPropagatedSubsumers().contains(negatedExpression))
			producer_.produce(input.getRoot(),
					new PossibleComposedSubsumerImpl(negatedExpression));
		return null;
	}

	@Override
	public Void visit(ForwardLink conclusion, Context input) {
		Root root = input.getRoot();
		Root fillerRoot = new Root(conclusion.getTarget());
		producer_.produce(fillerRoot, new BacktrackedBackwardLinkImpl(root,
				conclusion.getRelation()));
		return null;
	}
}
