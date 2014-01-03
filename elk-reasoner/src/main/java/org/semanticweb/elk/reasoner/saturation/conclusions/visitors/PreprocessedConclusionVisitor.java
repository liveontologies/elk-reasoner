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

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class PreprocessedConclusionVisitor<T> implements ConclusionVisitor<T> {

	final private ConclusionVisitor<?> preprocessor_;
	final private ConclusionVisitor<T> visitor_;

	public PreprocessedConclusionVisitor(ConclusionVisitor<?> preprocessor,
			ConclusionVisitor<T> visitor) {
		this.preprocessor_ = preprocessor;
		this.visitor_ = visitor;
	}

	@Override
	public T visit(ComposedSubsumer negSCE, Context context) {
		preprocessor_.visit(negSCE, context);
		return visitor_.visit(negSCE, context);
	}

	@Override
	public T visit(DecomposedSubsumer posSCE, Context context) {
		preprocessor_.visit(posSCE, context);
		return visitor_.visit(posSCE, context);
	}

	@Override
	public T visit(BackwardLink link, Context context) {
		preprocessor_.visit(link, context);
		return visitor_.visit(link, context);
	}

	@Override
	public T visit(ForwardLink link, Context context) {
		preprocessor_.visit(link, context);
		return visitor_.visit(link, context);
	}

	@Override
	public T visit(Contradiction bot, Context context) {
		preprocessor_.visit(bot, context);
		return visitor_.visit(bot, context);
	}

	@Override
	public T visit(Propagation propagation, Context context) {
		preprocessor_.visit(propagation, context);
		return visitor_.visit(propagation, context);
	}

	@Override
	public T visit(DisjointSubsumer disjointnessAxiom, Context context) {
		preprocessor_.visit(disjointnessAxiom, context);
		return visitor_.visit(disjointnessAxiom, context);
	}

}
