package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubClassConclusion;

/**
 * A skeleton for implementation of {@link ClassConclusion.Visitor}s using a common
 * (default) method
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public abstract class AbstractClassConclusionVisitor<I, O> extends
		AbstractSubClassConclusionVisitor<I, O> implements ClassConclusion.Visitor<I, O> {

	protected abstract O defaultVisit(ClassConclusion conclusion, I input);

	@Override
	O defaultVisit(SubClassConclusion subConclusion, I input) {
		return defaultVisit((ClassConclusion) subConclusion, input);
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
