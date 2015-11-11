package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;

/**
 * A {@link ClassConclusionVisitor} that runs a special preprocessor
 * {@link ClassConclusionVisitor} before every call of the provided
 * {@link ClassConclusionVisitor}. The returned result is taken from the letter.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of input parameter with which this visitor works
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public class PreprocessedConclusionVisitor<I, O> implements
		ClassConclusionVisitor<I, O> {

	/**
	 * a {@link ClassConclusionVisitor} that is called first
	 */
	final private ClassConclusionVisitor<I, ?> preprocessor_;
	/**
	 * a {@link ClassConclusionVisitor} that is called next and returns the output
	 */
	final private ClassConclusionVisitor<? super I, O> visitor_;

	public PreprocessedConclusionVisitor(ClassConclusionVisitor<I, ?> preprocessor,
			ClassConclusionVisitor<? super I, O> visitor) {
		this.preprocessor_ = preprocessor;
		this.visitor_ = visitor;
	}

	@Override
	public O visit(BackwardLink subConclusion, I input) {
		preprocessor_.visit(subConclusion, input);
		return visitor_.visit(subConclusion, input);
	}

	@Override
	public O visit(ComposedSubsumer conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(ContextInitialization conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(Contradiction conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(DecomposedSubsumer conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumer conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(ForwardLink conclusion, I input) {
		preprocessor_.visit(conclusion, input);
		return visitor_.visit(conclusion, input);
	}

	@Override
	public O visit(Propagation subConclusion, I input) {
		preprocessor_.visit(subConclusion, input);
		return visitor_.visit(subConclusion, input);
	}

	@Override
	public O visit(SubContextInitialization subConclusion, I input) {
		preprocessor_.visit(subConclusion, input);
		return visitor_.visit(subConclusion, input);
	}

}
