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

import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.implementation.NegatedSubsumerImpl;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.ConjectureNonSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleComposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.interfaces.PossibleDecomposedSubsumer;
import org.semanticweb.elk.alc.saturation.conclusions.visitors.LocalConclusionVisitor;

/**
 * 
 * A {@link LocalConclusionVisitor} works like a {@link RevertingVisitor} until
 * it encounters some {@link ComposedSubsumer} from the given {@link Set} of
 * {@link IndexedClass}es, after which it performs backtracking like
 * {@link BacktrackingVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SubsumptionExploringVisitor extends RevertingVisitor {

	private final ConclusionProducer producer_;

	private final Set<IndexedClass> subsumerCandidates_;

	/**
	 * if {@code true} go to the next branch on the first non-deterministic
	 * conclusion
	 */
	private boolean backtrack_ = false;

	public SubsumptionExploringVisitor(ConclusionProducer conclusionProducer,
			Set<IndexedClass> subsumerCandidates) {
		super(conclusionProducer);
		this.producer_ = conclusionProducer;
		this.subsumerCandidates_ = subsumerCandidates;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context input) {
		// does nothing by default
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer conclusion, Context input) {
		if (!backtrack_
				&& subsumerCandidates_.contains(conclusion.getExpression()))
			// we need to check if this subsumer is derived in other branches
			backtrack_ = true;
		// proceed as usual
		return defaultVisit(conclusion, input);
	}

	@Override
	public Boolean visit(PossibleComposedSubsumer conclusion, Context input) {
		if (backtrack_) {
			producer_.produce(new NegatedSubsumerImpl(conclusion
					.getExpression()));
			return false;
		}
		// else
		return super.visit(conclusion, input);
	}

	@Override
	public Boolean visit(PossibleDecomposedSubsumer conclusion, Context input) {
		if (backtrack_) {
			producer_.produce(new NegatedSubsumerImpl(conclusion
					.getExpression()));
			return false;
		}
		// else
		return super.visit(conclusion, input);
	}

	@Override
	public Boolean visit(ConjectureNonSubsumer conclusion, Context input) {
		if (backtrack_) {
			producer_.produce(new DecomposedSubsumerImpl(conclusion
					.getExpression()));
			return false;
		}
		// else
		return super.visit(conclusion, input);
	}

}
