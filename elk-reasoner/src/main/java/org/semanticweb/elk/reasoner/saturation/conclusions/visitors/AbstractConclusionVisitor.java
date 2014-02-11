package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.SubConclusion;

/**
 * A skeleton for implementation of {@link ConclusionVisitor}s using a common
 * (default) method
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public abstract class AbstractConclusionVisitor<I, O> extends
		AbstractSubConclusionVisitor<I, O> implements ConclusionVisitor<I, O> {

	abstract O defaultVisit(Conclusion conclusion, I input);

	@Override
	O defaultVisit(SubConclusion subConclusion, I input) {
		return defaultVisit((Conclusion) subConclusion, input);
	}

	@Override
	public O visit(ComposedSubsumer conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(ContextInitialization conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(Contradiction conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedSubsumer conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumer conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(ForwardLink conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

}
